package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.domain.TaskStatus
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.integration.llm.LlmClient
import org.springframework.stereotype.Component
import java.io.ByteArrayInputStream
import java.util.zip.ZipInputStream


@Component
class LlmWorker(
    detectRepo: ProblemDetectionRepository,
    private val fileService: FileService,
    private val llmClient: LlmClient,
    private val tx: TaskTxService,
    private val problemImportService: ProblemImportService
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

            // 3. unzip
            val parsed = parseLLMZip(response, parentId = id)

            // JSON -> Problem Mapping
            parsed.insulatorReportJson?.let { jsonBytes ->
                problemImportService.importFromReportJson(pd, ModelType.INSULATOR, jsonBytes)
            }
            parsed.railReportJson?.let { jsonBytes ->
                problemImportService.importFromReportJson(pd, ModelType.RAIL, jsonBytes)
            }
            parsed.nestReportJson?.let { jsonBytes ->
                problemImportService.importFromReportJson(pd, ModelType.NEST, jsonBytes)
            }

            // PDF -> File 저장 + Complete
            tx.completeLlm(id) { detection ->
                parsed.insulatorPDFId?.let { detection.insulatorReportFileId = it }
                parsed.railPDFId?.let { detection.railReportFileId = it }
                parsed.nestPDFId?.let { detection.nestReportFileId = it }
            }

        } catch (e: Exception) {
            tx.failLlm(id, e.message ?: e.javaClass.simpleName)
        }
    }

    override fun onError(pd: ProblemDetectionEntity, e: Exception) {
        val id = pd.id ?: return
        tx.failLlm(id, e.message ?: e.javaClass.simpleName)
    }

    private class LLMZipParsed(
        val insulatorPDFId: Long? = null,
        val railPDFId: Long? = null,
        val nestPDFId: Long? = null,
        val insulatorReportJson: ByteArray? = null,
        val railReportJson: ByteArray? = null,
        val nestReportJson: ByteArray? = null,
        )

    private fun parseLLMZip(zipBytes: ByteArray, parentId: Long?): LLMZipParsed {
        var insPdf: Long? = null
        var railPdf: Long? = null
        var nestPdf: Long? = null
        var insJson: ByteArray? = null
        var railJson: ByteArray? = null
        var nestJson: ByteArray? = null

        fun detectModel(nameLower: String): ModelType? = when {
            INSULATOR_KEYS.any { nameLower.contains(it) } -> ModelType.INSULATOR
            RAIL_KEYS.any { nameLower.contains(it) } -> ModelType.RAIL
            NEST_KEYS.any { nameLower.contains(it) } -> ModelType.NEST
            else -> null
        }

        ZipInputStream(ByteArrayInputStream(zipBytes)).use { zis ->
            while (true) {
                val entry = zis.nextEntry ?: break
                if (entry.isDirectory) continue

                val entryName = entry.name
                val filename = entryName.substringAfterLast('/')
                val lower = filename.lowercase()
                val ext = filename.substringAfterLast('.', "").lowercase()

                val model = detectModel(lower)
                if(model == null) {
                    zis.closeEntry()
                    continue
                }

                val bytes = zis.readBytes()

                when (ext) {
                    "pdf" -> {
                        val uploaded = fileService.uploadBytes(
                            bytes = bytes,
                            originalFilename = "llm/$filename",
                            contentType = "application/pdf",
                            fileType = FileType.PDF,
                            parentId = parentId
                        )
                        when (model) {
                            ModelType.INSULATOR -> insPdf = uploaded.fileId
                            ModelType.RAIL -> railPdf = uploaded.fileId
                            ModelType.NEST -> nestPdf = uploaded.fileId
                        }
                    }
                    "json" -> {
                        when (model) {
                            ModelType.INSULATOR -> insJson = bytes
                            ModelType.RAIL -> railJson = bytes
                            ModelType.NEST -> nestJson = bytes
                        }
                    }
                }

                zis.closeEntry()
            }
        }

        return LLMZipParsed(
            insulatorPDFId = insPdf,
            railPDFId = railPdf,
            nestPDFId = nestPdf,
            insulatorReportJson = insJson,
            railReportJson = railJson,
            nestReportJson = nestJson
        )
    }
}