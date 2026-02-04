package kr.co.raildock.raildock_server.feedback.service

import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.feedback.dto.FineTuningJobResponse
import kr.co.raildock.raildock_server.feedback.enum.FeedbackStatus
import kr.co.raildock.raildock_server.feedback.repository.FeedbackRepository
import kr.co.raildock.raildock_server.file.service.FileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.OutputStream
import java.time.LocalDateTime
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
@Transactional(readOnly = true)
class FineTuningServiceImpl(
    private val feedbackRepository: FeedbackRepository,
    private val fileService: FileService
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

                zos.putNextEntry(ZipEntry("$category/${feedback.id}_image.jpg"))
                fileService.openStream(imageId).use { it.copyTo(zos) }
                zos.closeEntry()

                zos.putNextEntry(ZipEntry("$category/${feedback.id}_label.json"))
                fileService.openStream(jsonId).use { it.copyTo(zos) }
                zos.closeEntry()

                // 🔥 여기서 COMPLETE 처리
                feedback.feedbackStatus = FeedbackStatus.COMPLETE
            }
        }
    }

    override fun startFineTuning(): FineTuningJobResponse {
        return FineTuningJobResponse(
            jobId = UUID.randomUUID(),
            status = "REQUESTED",
            requestedAt = LocalDateTime.now()
        )
    }

    private fun resolveCategory(model: ModelType): String =
        when (model) {
            ModelType.INSULATOR -> "애자"
            ModelType.RAIL      -> "레일"
            ModelType.NEST      -> "둥지"
        }
}
