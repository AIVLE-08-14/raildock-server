package kr.co.raildock.raildock_server.detect.dto

data class ProblemDetectionGetResponse(
    val id: Long,
    val name: String,
    val section: String,
    val datetime: String,
    val direction: String,
    val weather: String?,
    val temperature: Int?,
    val humidity: Int?,
    val metadataUrl: String?,
    val insulatorVideoUrl: String?,
    val railVideoUrl: String?,
    val nestVideoUrl: String?,
    val taskStatus: String,
    val errorMessage: String?,
)
