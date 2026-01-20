package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.DetectJobEntity
import kr.co.raildock.raildock_server.detect.domain.DetectJobStatus
import kr.co.raildock.raildock_server.detect.dto.DetectJobCreateResponse
import kr.co.raildock.raildock_server.detect.dto.DetectJobGetResponse
import kr.co.raildock.raildock_server.detect.repository.DetectJobRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.UUID

@Service
class DetectService(
    private val jobRepository: DetectJobRepository,
) {

    private val baseDir: Path = Path.of("data/videos")

    fun createJob(video: MultipartFile, metadata: String): DetectJobCreateResponse{
        Files.createDirectories(baseDir)

        val originalName = video.originalFilename ?: "video.mp4"
        val ext = originalName.substringAfterLast('.', "mp4")
        val storedName = "${UUID.randomUUID()}.$ext"
        val storedPath = baseDir.resolve(storedName)

        video.inputStream.use { input ->
            Files.copy(input, storedPath, StandardCopyOption.REPLACE_EXISTING)
        }

        val job = jobRepository.save(
            DetectJobEntity(
                originalFilename = originalName,
                videoPath = storedPath.toString(),
                metadataJson = metadata,
                status = DetectJobStatus.PENDING
            )
        )
        return DetectJobCreateResponse(
            jobId = job.id!!,
            status = job.status.name
        )
    }
    fun getJob(jobId: Long): DetectJobGetResponse {
        val job = jobRepository.findById(jobId).orElseThrow { NoSuchElementException("Job with ID $jobId not found") }
        return DetectJobGetResponse(
            jobId = job.id!!,
            status = job.status.name,
            createdAt = job.createdAt,
            startedAt = job.startedAt,
            completedAt = job.completedAt,
            errorMessage = job.errorMessage
        )
    }
}