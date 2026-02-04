package kr.co.raildock.raildock_server.feedback.dto

import java.time.LocalDateTime
import java.util.UUID

data class EngineerFineTuningPayload(
    val zipFileName: String,
    val zipFilePath: String,
    val generatedAt: LocalDateTime,
    val totalSampleCount: Int,
    val categorySummary: Map<String, Int>
)

data class FineTuningJobResponse(
    val jobId: UUID,
    val status: String,
    val requestedAt: LocalDateTime
)