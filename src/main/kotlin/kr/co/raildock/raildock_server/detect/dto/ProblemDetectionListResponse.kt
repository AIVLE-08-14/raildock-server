package kr.co.raildock.raildock_server.detect.dto

data class ProblemDetectionListResponse(
    val items: List<ProblemDetectionListItem>
)

data class ProblemDetectionListItem(
    val id: Long,
    val name: String,
    val createdAt: String,
    val videoTaskStatus: String,
    val llmTaskStatus: String
)