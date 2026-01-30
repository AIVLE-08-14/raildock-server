package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.domain.TaskStatus
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.integration.llm.LlmClient
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Component
class LlmWorker(
    detectRepo: ProblemDetectionRepository,
    private val fileService: FileService,
    private val llmClient: LlmClient
) : AbstractTaskWorker(detectRepo) {

    @Transactional
    fun runOnce(): Boolean = super.processOnePending()

    override fun findCandidate(): ProblemDetectionEntity? {
        return detectRepo.findTopByLlmTaskStatusOrderByCreatedAtDesc(TaskStatus.PENDING)
    }

    override fun tryStart(id: Long): Int {
        return detectRepo.tryStartLlm(
            id = id,
            from = TaskStatus.PENDING,
            to = TaskStatus.RUNNING
        )
    }

    override fun execute(pd: ProblemDetectionEntity) {
        // 1. LLM 처리에 필요한 입력 파일 준비
        val resultZipFileId = pd.videoDetectedZipFileId
        val metadataFileId = pd.metadataFileId

        // 혹시 모를 예외처리(로직상 발생하지 않아야 함)
        if (resultZipFileId == null || metadataFileId == null) {
            pd.llmTaskStatus = TaskStatus.FAILED
            pd.taskErrorMessage = "LLM input not ready: resultZipFileId or metadataFileId is null"
            return
        }

        val videoUrl = fileService.getDownloadUrl(resultZipFileId)
        val metadataUrl = fileService.getDownloadUrl(metadataFileId)

        // 2. FastAPI(LLM) 요청
        val response = llmClient.pipeline.processVideo(
            videoUrl = videoUrl,
            originalMetadataUrl = metadataUrl,
            generatePdf = true,
            skipReview = false
        )

        // 3. 결과 처리
        /**
         * 여기서는 일단 "LLM 처리 성공"까지만 책임진다.
         * - response.pdfPaths
         * - response.results
         * 는 다음 단계(ProblemWorker)에서 처리하는 게 가장 깔끔
         */

        // TODO (다음 단계):
        // pd.llmResultSummary = response.summary
        // pd.llmPdfPaths = response.pdfPaths

        // 4. 상태 갱신
        pd.llmTaskStatus = TaskStatus.COMPLETED
        pd.taskErrorMessage = null
    }

    override fun onError(pd: ProblemDetectionEntity, e: Exception) {
        pd.llmTaskStatus = TaskStatus.FAILED
        pd.taskErrorMessage = (e.message ?: e.javaClass.simpleName).take(500)
    }
}