package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.domain.TaskStatus
import kr.co.raildock.raildock_server.integration.vision.VisionInferRequest
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.integration.vision.VisionClientImpl
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DetectWorker(
    detectRepo: ProblemDetectionRepository,
    private val fileService: FileService,
    private val fastApiClient: VisionClientImpl,
    private val tx: TaskTxService
) : AbstractTaskWorker(detectRepo) {

    fun runOnce(): Boolean = super.processOnePending()

    override fun findCandidate(): ProblemDetectionEntity? {
        return detectRepo.findTopByVideoTaskStatusOrderByCreatedAtDesc(TaskStatus.PENDING)
    }

    override fun tryStart(id: Long): Int =
        if (tx.markVideoRunning(id)) 1 else 0

    override fun execute(pd: ProblemDetectionEntity) {

        val id = pd.id ?: return

        try {
            // 1. 영상 파일 다운로드 URL 확보
            val railUrl = pd.railVideoFileId?.let { fileService.getDownloadUrl(it) }
            val insulatorUrl = pd.insulatorVideoFileId?.let { fileService.getDownloadUrl(it) }
            val nestUrl = pd.nestVideoFileId?.let { fileService.getDownloadUrl(it) }

            // 2. URL 입력 확인
            if (railUrl == null && insulatorUrl == null && nestUrl == null) {
                tx.failVideo(id, "No input videos found. At least one video is required.")
                return

            }

            // 3. Vision API 영상 분석 요청
            val req = VisionInferRequest(
                rail_mp4 = railUrl,
                insulator_mp4 = insulatorUrl,
                nest_mp4 = nestUrl,
                conf = 0.25,
                iou = 0.7,
                stride = 5
            )

            val zipBytes = fastApiClient.infer(req)

            // 4. 결과 ZIP 업로드 + 저장
            val result = fileService.uploadBytes(
                bytes = zipBytes,
                originalFilename = "detection_result_${pd.id}.zip",
                contentType = "application/zip",
                fileType = FileType.ZIP
            )

            // 5. 완료 처리
            tx.completeVideo(id) { entity ->
                entity.videoDetectedZipFileId = result.fileId

                val llmReady = (entity.metadataFileId != null)
                if (llmReady && entity.llmTaskStatus == TaskStatus.CREATED) {
                    entity.llmTaskStatus = TaskStatus.PENDING
                }
            }
        } catch (e: Exception) {
            tx.failVideo(id, "${e.message}")
        }
    }

    override fun onError(pd: ProblemDetectionEntity, e: Exception) {
        pd.videoTaskStatus = TaskStatus.FAILED
        pd.taskErrorMessage = (e.message ?: e.javaClass.simpleName).take(500)
    }
}