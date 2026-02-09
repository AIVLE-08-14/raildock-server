package kr.co.raildock.raildock_server.feedback.service

import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.feedback.dto.FinetuneConfigDto
import kr.co.raildock.raildock_server.feedback.dto.FineTuningJobResponse
import kr.co.raildock.raildock_server.feedback.dto.TaskSummaryDto
import kr.co.raildock.raildock_server.feedback.enum.FeedbackStatus
import kr.co.raildock.raildock_server.feedback.repository.FeedbackRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.integration.vision.FeedbackUrlRequest
import kr.co.raildock.raildock_server.integration.vision.VisionClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.time.LocalDateTime
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
@Transactional(readOnly = true)
class FineTuningServiceImpl(
    private val feedbackRepository: FeedbackRepository,
    private val fileService: FileService,
    private val visionClient: VisionClient
) : FineTuningService {

    @Transactional
    override fun writeZipTo(outputStream: OutputStream) {

        val feedbacks =
            feedbackRepository.findAllByFeedbackStatus(FeedbackStatus.PENDING)

        ZipOutputStream(outputStream).use { zos ->

            feedbacks.forEach { feedback ->

                val category = resolveCategory(feedback.model)

                val imageId = feedback.sourceImageId ?: error("sourceImageId 없음")
                val jsonId  = feedback.boundingBoxJsonId ?: error("boundingBoxJsonId 없음")

                // Vision AI 기대 형식: data/{task}/origin/*.jpg, data/{task}/json/*.json
                zos.putNextEntry(ZipEntry("data/$category/origin/${feedback.id}.jpg"))
                fileService.openStream(imageId).use { it.copyTo(zos) }
                zos.closeEntry()

                zos.putNextEntry(ZipEntry("data/$category/json/${feedback.id}.json"))
                fileService.openStream(jsonId).use { it.copyTo(zos) }
                zos.closeEntry()

                // 🔥 여기서 COMPLETE 처리
                feedback.feedbackStatus = FeedbackStatus.COMPLETE
            }
        }
    }

    @Transactional
    override fun startFineTuning(): FineTuningJobResponse {
        // 1. ZIP 데이터 생성 (메모리에)
        val zipBytes = ByteArrayOutputStream().use { baos ->
            writeZipToInternal(baos)
            baos.toByteArray()
        }

        if (zipBytes.isEmpty()) {
            return FineTuningJobResponse(
                jobId = "",
                status = "NO_DATA",
                summary = null,
                config = null,
                requestedAt = LocalDateTime.now()
            )
        }

        // 2. S3에 업로드
        val uploadResult = fileService.uploadBytes(
            bytes = zipBytes,
            originalFilename = "finetune_data_${System.currentTimeMillis()}.zip",
            contentType = "application/zip",
            fileType = FileType.ZIP
        )

        // 3. 다운로드 URL 획득
        val zipUrl = fileService.getDownloadUrl(uploadResult.fileId)

        // 4. Vision AI /feedback_url 호출
        val response = visionClient.feedbackUrl(
            FeedbackUrlRequest(
                zipUrl = zipUrl,
                overwrite = false
            )
        )

        // 5. 응답 변환
        val summaryDto = response.summary?.mapValues { (_, v) ->
            TaskSummaryDto(pairs = v.pairs, copied = v.copied, skipped = v.skipped)
        }

        val configDto = response.finetuneConfig?.let {
            FinetuneConfigDto(epochs = it.epochs, batch = it.batch, imgsz = it.imgsz)
        }

        return FineTuningJobResponse(
            jobId = response.finetuneJobId ?: "",
            status = if (response.ok) "RUNNING" else "FAILED",
            summary = summaryDto,
            config = configDto,
            requestedAt = LocalDateTime.now()
        )
    }

    /**
     * 내부용 ZIP 생성 (feedbackStatus 변경 없이)
     */
    private fun writeZipToInternal(outputStream: OutputStream) {
        val feedbacks =
            feedbackRepository.findAllByFeedbackStatus(FeedbackStatus.PENDING)

        ZipOutputStream(outputStream).use { zos ->
            feedbacks.forEach { feedback ->
                val category = resolveCategory(feedback.model)

                val imageId = feedback.sourceImageId ?: return@forEach
                val jsonId  = feedback.boundingBoxJsonId ?: return@forEach

                zos.putNextEntry(ZipEntry("data/$category/origin/${feedback.id}.jpg"))
                fileService.openStream(imageId).use { it.copyTo(zos) }
                zos.closeEntry()

                zos.putNextEntry(ZipEntry("data/$category/json/${feedback.id}.json"))
                fileService.openStream(jsonId).use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
    }

    private fun resolveCategory(model: ModelType): String =
        when (model) {
            ModelType.INSULATOR -> "insulator"
            ModelType.RAIL      -> "rail"
            ModelType.NEST      -> "nest"
        }
}
