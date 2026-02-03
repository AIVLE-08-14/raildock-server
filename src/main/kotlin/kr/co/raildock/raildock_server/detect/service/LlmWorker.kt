package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.domain.TaskStatus
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.integration.llm.LlmClient
import org.springframework.stereotype.Component


@Component
class LlmWorker(
    detectRepo: ProblemDetectionRepository,
    private val fileService: FileService,
    private val llmClient: LlmClient,
    private val tx: TaskTxService
) : AbstractTaskWorker(detectRepo) {

    private val INSULATOR_KEYS = listOf("insulator", "애자")
    private val RAIL_KEYS = listOf("rail", "선로")
    private val NEST_KEYS = listOf("nest", "둥지")

    fun runOnce(): Boolean = super.processOnePending()

    override fun findCandidate(): ProblemDetectionEntity? =
        detectRepo.findTopByLlmTaskStatusOrderByCreatedAtDesc(TaskStatus.PENDING)

    override fun tryStart(id: Long): Int =
        if (tx.markLlmRunning(id)) 1 else 0

    override fun execute(pd: ProblemDetectionEntity) {
        val id = pd.id ?: return

        try {
            // 1. 입력 준비 (트랜잭션 밖에서 값만 읽기)
            val resultZipFileId = pd.videoDetectedZipFileId
            val metadataFileId = pd.metadataFileId

            if (resultZipFileId == null || metadataFileId == null) {
                tx.failLlm(id, "LLM input not ready: resultZipFileId or metadataFileId is null")
                return
            }

            val videoUrl = fileService.getDownloadUrl(resultZipFileId)
            val metadataUrl = fileService.getDownloadUrl(metadataFileId)

            // 2. FastAPI(LLM) 호출
            val response: ByteArray = llmClient.pipeline.processVideo(
                videoUrl = videoUrl,
                originalMetadataUrl = metadataUrl,
                generatePdf = true,
                skipReview = false
            )

            // 3. unzip + upload
            val outputs = fileService.unzipAndUpload(response, parentId = id)

            // 4. REQUIRES_NEW로 확실하게 DB 저장 - 파일 이름 기반 매핑
            tx.completeLlm(id) { entity ->
                outputs.forEach { output ->
                    val name = output.originalFilename.lowercase()

                    fun containsAny(keys: List<String>) = keys.any { key -> name.contains(key) }

                    when {
                        containsAny(INSULATOR_KEYS) && name.endsWith(".pdf") ->
                            entity.insulatorReportFileId = output.fileId
                        containsAny(RAIL_KEYS) && name.endsWith(".pdf") ->
                            entity.railReportFileId = output.fileId
                        containsAny(NEST_KEYS) && name.endsWith(".pdf") ->
                            entity.nestReportFileId = output.fileId

                        containsAny(INSULATOR_KEYS) && name.endsWith(".json") ->
                            entity.insulatorJsonId = output.fileId
                        containsAny(RAIL_KEYS) && name.endsWith(".json") ->
                            entity.railJsonId = output.fileId
                        containsAny(NEST_KEYS) && name.endsWith(".json") ->
                            entity.nestJsonId = output.fileId
                    }
                }
            }
        } catch (e: Exception) {
            tx.failLlm(id, e.message ?: e.javaClass.simpleName)
        }
    }

    override fun onError(pd: ProblemDetectionEntity, e: Exception) {
        val id = pd.id ?: return
        tx.failLlm(id, e.message ?: e.javaClass.simpleName)
    }
}