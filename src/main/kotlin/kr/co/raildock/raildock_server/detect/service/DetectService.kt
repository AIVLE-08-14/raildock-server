package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import kr.co.raildock.raildock_server.detect.domain.DetectJobStatus
import kr.co.raildock.raildock_server.detect.domain.DetectionVideoEntity
import kr.co.raildock.raildock_server.detect.domain.VideoType
import kr.co.raildock.raildock_server.detect.dto.DetectCreateResponse
import kr.co.raildock.raildock_server.detect.dto.DetectJobGetResponse
import kr.co.raildock.raildock_server.detect.dto.InferResult
import kr.co.raildock.raildock_server.detect.dto.VideoTaskDto
import kr.co.raildock.raildock_server.detect.repository.DetectionVideoRepository
import kr.co.raildock.raildock_server.detect.repository.ProblemDetectionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.OffsetDateTime
import java.util.UUID

@Service
class DetectService(
    private val detectRepo: ProblemDetectionRepository,
    private val videoRepo: DetectionVideoRepository
) {
    private val baseVideoDir: Path = Path.of("data/videos")
    private val baseMetaDir: Path = Path.of("data/metadata")

    @Transactional
    fun create(
        name: String,
        section: String,
        datetime: String,
        direction: String,
        humidity: Int?,
        temperature: Int?,
        weather: String?,
        metadata: MultipartFile?,
        insulatorVideo: MultipartFile?,
        railVideo: MultipartFile?,
        nestVideo: MultipartFile?
    ): DetectCreateResponse{
        val hasAnyVideo = (insulatorVideo != null) || (railVideo != null) || (nestVideo != null)
        require(hasAnyVideo) { "At least one video is required" }

        val dt = OffsetDateTime.parse(datetime) // ISO-8601

        val pd = detectRepo.save(
            ProblemDetectionEntity(
                name = name,
                section = section,
                datetime = dt,
                direction = direction,
                humidity = humidity,
                temperature = temperature,
                weather = weather
            )
        )

        if (metadata != null && !metadata.isEmpty) {
            Files.createDirectories(baseVideoDir)
            val metaPath = baseMetaDir.resolve("${pd.id}.json")
            metadata.inputStream.use{input ->
                Files.copy(input, metaPath, StandardCopyOption.REPLACE_EXISTING)
            }
            pd.metadataUrl = metaPath.toString()
        }

        Files.createDirectories(baseVideoDir)

        val createdVideos = mutableListOf<DetectionVideoEntity>()

        fun saveOne(type: VideoType, file: MultipartFile){
            val originalName = file.originalFilename ?: "video.mp4"
            val ext = originalName.substringAfterLast('.', "mp4")
            val storedName = "${UUID.randomUUID()}.$ext"

            val dir = baseVideoDir.resolve(pd.id.toString()).resolve(type.name)
            Files.createDirectories(dir)

            val storedPath = dir.resolve(storedName)

            file.inputStream.use { input ->
                Files.copy(input, storedPath, StandardCopyOption.REPLACE_EXISTING)
            }

            val videoEntity = videoRepo.save(
                DetectionVideoEntity(
                    problemDetection = pd,
                    videoType = type,
                    videoURL = storedPath.toString(),
                    originalFilename = originalName,
                    taskStatus = DetectJobStatus.PENDING
                )
            )
            createdVideos += videoEntity
        }

        if (insulatorVideo != null && !insulatorVideo.isEmpty) saveOne(VideoType.INSULATOR, insulatorVideo)
        if (railVideo != null && !railVideo.isEmpty) saveOne(VideoType.RAIL, railVideo)
        if (nestVideo != null && !nestVideo.isEmpty) saveOne(VideoType.NEST, nestVideo)

        // 5) 응답
        return DetectCreateResponse(
            detectionId = pd.id!!,
            videos = createdVideos.map {
                VideoTaskDto(
                    videoId = it.id!!.toString(),
                    videoType = it.videoType.name,
                    taskStatus = it.taskStatus.name
                )
            }
        )
    }
}