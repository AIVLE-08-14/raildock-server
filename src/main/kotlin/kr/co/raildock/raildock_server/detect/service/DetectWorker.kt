package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.DetectStatus
import kr.co.raildock.raildock_server.detect.dto.FastAPIInferRequest
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DetectWorker(
    private val detectRepo: ProblemDetectionRepository,
    private val fileService: FileService,
    private val fastApiClient: FastApiClientImpl,
) {

    @Transactional
    fun processOnePending(): Boolean {

        // 1. 후보 조회 (가장 최근 PENDING 작업 하나 조회)
        val candidate = detectRepo.findTopByTaskStatusOrderByDatetimeDesc(DetectStatus.PENDING)
            ?: return false

        // 2. 선점(RUNNING 으로 상태 변경 시도)
        val claimed = detectRepo.tryStart(
            id = candidate.id!!,
            from = DetectStatus.PENDING,
            to = DetectStatus.RUNNING
        )
        if (claimed == 0) {
            return true // 선점 실패시 다음 루프에서 시도
        }

        // 3. 선점 성공시 재 조회(영속 상태)
        val pd = detectRepo.findById(candidate.id!!).orElseThrow()

        try{

            // 4. 파일 다운로드 URL 받기
            val railUrl = pd.railVideoFileId?.let { fileService.getDownloadUrl(it) }
            val insulatorUrl = pd.insulatorVideoFileId?.let { fileService.getDownloadUrl(it) }
            val nestUrl = pd.nestVideoFileId?.let { fileService.getDownloadUrl(it) }

            // 최소 조건 체크
            if (railUrl == null && insulatorUrl == null && nestUrl == null) {
                pd.taskStatus = DetectStatus.FAILED
                pd.errorMessage = "No Video fileId found. At least one video is required for processing"
                return true
            }

            // 5. FastAPI 에 추론 요청
            val req = FastAPIInferRequest(
                rail_mp4 = railUrl,
                insulator_mp4 = insulatorUrl,
                nest_mp4 = nestUrl,
                conf = 0.25,
                iou = 0.7,
                stride = 5
            )

            // TODO: Zip 파일 처리 한거 나중에 고려
            val zipBytes = fastApiClient.infer(req)
            val result = fileService.uploadBytes(
                bytes = zipBytes,
                originalFilename = "detection_result_${pd.id}.zip",
                contentType = "application/zip",
                fileType = FileType.ZIP
            )
            pd.resultZipFileId = result.fileId

            // 6. 결과 파일Id 저장 및 상태 변경
            pd.taskStatus = DetectStatus.COMPLETED
            pd.errorMessage = null
            return true
        } catch(e: Exception) {
            // 7. 예외 처리
            pd.taskStatus = DetectStatus.FAILED
            pd.errorMessage = (e.message ?: e.javaClass.simpleName).take(500)
            return true
        }

    }
}