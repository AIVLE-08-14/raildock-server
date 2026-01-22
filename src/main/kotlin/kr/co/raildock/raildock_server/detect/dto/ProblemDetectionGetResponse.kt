package kr.co.raildock.raildock_server.detect.dto

data class ProblemDetectionGetResponse(
    val id: Long,
    val name: String,
    val section: String,
    val datetime: String,
    val direction: String,
    val weather: String?,
    val temperature: Int?,
    val videos: List<DetectVideoSummaryDto>
)

data class DetectVideoSummaryDto(
    val videoId: Long,
    val videoType: String,
    val status: String
)
