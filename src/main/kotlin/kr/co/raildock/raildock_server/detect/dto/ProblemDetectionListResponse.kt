package kr.co.raildock.raildock_server.detect.dto

data class ProblemDetectionListResponse(
    val items: List<ProblemDetectionListItem>
)

data class ProblemDetectionListItem(
    val id: Long,
    val name: String,
    val section: String,
    val datetime: String,
    val direction: String,
    val weather: String?,
    val humidity: Int?,
    val temperature: Int?,
    val videos: List<DetectVideoSummaryDto>
)