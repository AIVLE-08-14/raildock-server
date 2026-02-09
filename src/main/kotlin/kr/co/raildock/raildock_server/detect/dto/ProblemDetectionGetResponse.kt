package kr.co.raildock.raildock_server.detect.dto

import kr.co.raildock.raildock_server.problem.dto.ProblemModelSummaryDto

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
    val problems : DetectionProblemSummaryResponse?,
)

data class DetectionProblemSummaryResponse(
    val insulator: List<ProblemModelSummaryDto>,
    val rail: List<ProblemModelSummaryDto>,
    val nest: List<ProblemModelSummaryDto>
)