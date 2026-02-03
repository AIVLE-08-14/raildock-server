package kr.co.raildock.raildock_server.detect.dto

data class ProblemDetectionGetResponse(
    val id: Long,
    val name: String,
    val createdAt: String,
    val updatedAt: String,

    val metadataUrl: String?,
    val insulatorVideoUrl: String?,
    val railVideoUrl: String?,
    val nestVideoUrl: String?,

    val videoTaskStatus: String,
    val taskErrorMessage: String?,
    val videoResultZipUrl: String?,
    val llmTaskStatus: String,

    val insulatorReportUrl: String?,
    val railReportUrl: String?,
    val nestReportUrl: String?,

    val insulatorJsonUrl: String?,
    val railJsonUrl: String?,
    val nestJsonUrl: String?
)
