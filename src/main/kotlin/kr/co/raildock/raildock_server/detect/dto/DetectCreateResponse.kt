package kr.co.raildock.raildock_server.detect.dto

data class DetectCreateResponse (
    val detectionId: Long,
)

data class VideoTaskDto (
    val videoId: String,
    val videoType: String,
    val taskStatus: String
)