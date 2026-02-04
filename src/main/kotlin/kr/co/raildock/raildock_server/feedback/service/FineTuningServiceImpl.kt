package kr.co.raildock.raildock_server.feedback.service

import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.feedback.dto.EngineerFineTuningPayload
import kr.co.raildock.raildock_server.feedback.dto.FineTuningJobResponse
import kr.co.raildock.raildock_server.feedback.enum.FeedbackStatus
import kr.co.raildock.raildock_server.feedback.repository.FeedbackRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
@Transactional
class FineTuningServiceImpl(
    private val feedbackRepository: FeedbackRepository,
    private val fileService: FileService
) : FineTuningService {
    override fun buildFineTuningData(): EngineerFineTuningPayload {

        val feedbacks =
            feedbackRepository.findAllByFeedbackStatus(FeedbackStatus.COMPLETE)

        val zipByteArray = ByteArrayOutputStream()

        val categoryCount = mutableMapOf<String, Int>()
        var totalCount = 0

        ZipOutputStream(zipByteArray).use { zos ->

            feedbacks.forEach { feedback ->

                val category = resolveCategory(feedback.model)

                val imageStream = fileService.openStream(
                    feedback.sourceImageId
                        ?: throw IllegalStateException("sourceImageId 없음")
                )

                val jsonStream = fileService.openStream(
                    feedback.boundingBoxJsonId
                        ?: throw IllegalStateException("boundingBoxJsonId 없음")
                )

                val imageEntryName =
                    "$category/${feedback.id}_image.jpg"
                val jsonEntryName =
                    "$category/${feedback.id}_label.json"

                zos.putNextEntry(ZipEntry(imageEntryName))
                imageStream.use { it.copyTo(zos) }
                zos.closeEntry()

                zos.putNextEntry(ZipEntry(jsonEntryName))
                jsonStream.use { it.copyTo(zos) }
                zos.closeEntry()

                categoryCount[category] =
                    categoryCount.getOrDefault(category, 0) + 1
                totalCount++
            }
        }

        val zipBytes = zipByteArray.toByteArray()
        val zipFileName = "fine_tuning_${System.currentTimeMillis()}.zip"

        val uploadedZip = fileService.uploadBytes(
            bytes = zipBytes,
            originalFilename = zipFileName,
            contentType = "application/zip",
            fileType = FileType.ZIP
        )

        return EngineerFineTuningPayload(
            zipFileName = zipFileName,
            zipFilePath = fileService.getDownloadUrl(uploadedZip.fileId),
            generatedAt = LocalDateTime.now(),
            totalSampleCount = totalCount,
            categorySummary = categoryCount
        )
    }

    override fun startFineTuning(): FineTuningJobResponse {
        // TODO: 실제 AI 서버 연동 예정
        return FineTuningJobResponse(
            jobId = UUID.randomUUID(),
            status = "REQUESTED",
            requestedAt = LocalDateTime.now()
        )
    }

    /**
     * problemId → 애자 / 레일 / 둥지
     * 현재는 임시 로직
     */
    private fun resolveCategory(model: ModelType): String =
        when (model) {
            ModelType.INSULATOR -> "애자"
            ModelType.RAIL      -> "레일"
            ModelType.NEST      -> "둥지"
        }

    /**
     * 디렉토리를 ZIP으로 압축
     */
    private fun zipDirectory(sourceDir: File, zipFile: File) {
        ZipOutputStream(FileOutputStream(zipFile)).use { zos ->
            sourceDir.walkTopDown()
                .filter { it.isFile }
                .forEach { file ->
                    val entryName = sourceDir.toPath().relativize(file.toPath()).toString()
                    zos.putNextEntry(ZipEntry(entryName))
                    file.inputStream().copyTo(zos)
                    zos.closeEntry()
                }
        }
    }
}
