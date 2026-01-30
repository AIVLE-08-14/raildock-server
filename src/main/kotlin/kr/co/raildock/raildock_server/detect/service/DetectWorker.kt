package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.domain.TaskStatus
import kr.co.raildock.raildock_server.detect.dto.FastAPIInferRequest
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DetectWorker(
    detectRepo: ProblemDetectionRepository,
    private val fileService: FileService,
    private val fastApiClient: FastApiClientImpl,
) : AbstractTaskWorker(detectRepo) {

    @Transactional
    fun runOnce(): Boolean = super.processOnePending()

    override fun findCandidate(): ProblemDetectionEntity? {
        return detectRepo.findTopByVideoTaskStatusOrderByCreatedAtDesc(TaskStatus.PENDING)
    }

    override fun tryStart(id: Long): Int {
        return detectRepo.tryStartVideo(
            id = id,
            from = TaskStatus.PENDING,
            to = TaskStatus.RUNNING
        )
    }

    override fun execute(pd: ProblemDetectionEntity) {
        // 1) 파일 다운로드 URL 받기
        val railUrl = pd.railVideoFileId?.let { fileService.getDownloadUrl(it) }
        val insulatorUrl = pd.insulatorVideoFileId?.let { fileService.getDownloadUrl(it) }
        val nestUrl = pd.nestVideoFileId?.let { fileService.getDownloadUrl(it) }

        // 최소 조건 체크
        if (railUrl == null && insulatorUrl == null && nestUrl == null) {
            pd.videoTaskStatus = TaskStatus.FAILED
            pd.taskErrorMessage = "No video fileId found. At least one video is required."
            return
        }

        // 2) FastAPI(VideoDetection) 요청
        val req = FastAPIInferRequest(
            rail_mp4 = railUrl,
            insulator_mp4 = insulatorUrl,
            nest_mp4 = nestUrl,
            conf = 0.25,
            iou = 0.7,
            stride = 5
        )

        val zipBytes = fastApiClient.infer(req)

        // 3) 결과 ZIP 업로드 + 저장
        val result = fileService.uploadBytes(
            bytes = zipBytes,
            originalFilename = "detection_result_${pd.id}.zip",
            contentType = "application/zip",
            fileType = FileType.ZIP
        )
        pd.videoDetectedZipFileId = result.fileId

        // 4) 상태 갱신
        pd.videoTaskStatus = TaskStatus.COMPLETED
        pd.taskErrorMessage = null

        // 5) LLM 트리거: 입력이 준비됐으면 CREATED(or WAITING) -> PENDING
        val llmReady = (pd.videoDetectedZipFileId != null && pd.metadataFileId != null)
        if (llmReady && pd.llmTaskStatus == TaskStatus.CREATED) {
            pd.llmTaskStatus = TaskStatus.PENDING
        }
    }

    override fun onError(pd: ProblemDetectionEntity, e: Exception) {
        pd.videoTaskStatus = TaskStatus.FAILED
        pd.taskErrorMessage = (e.message ?: e.javaClass.simpleName).take(500)
    }
}