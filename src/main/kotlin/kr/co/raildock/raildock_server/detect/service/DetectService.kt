package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.dto.DetectJobCreateResponse
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

@Service
class DetectService {

    private val baseDir: Path = Path.of("data/videos")

    fun createJob(video: MultipartFile, metadata: String): DetectJobCreateResponse{
        Files.createDirectories(baseDir)
        val originalName = video.originalFilename ?: "video.mp4"
        val ext = originalName.substringAfterLast('.', "mp4")
        val storedName = "${UUID.randomUUID()}_$ext"
        val storedPath = baseDir.resolve(storedName)

        video.inputStream.use { input ->
            Files.copy(input, storedPath)
        }

        val jobId = 1L // Tmp

        return DetectJobCreateResponse(
            jobId = jobId,
            status = "PENDING"
        )
    }
}