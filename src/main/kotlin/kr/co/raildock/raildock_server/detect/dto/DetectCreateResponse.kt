package kr.co.raildock.raildock_server.detect.dto

data class DetectCreateResponse (
    val detectionId: Long,
    val videos: List<VideoTaskDto>
)

data class VideoTaskDto (
    val videoId: String,
    val videoType: String,
    val taskStatus: String
)