package kr.co.raildock.raildock_server.feedback.dto

import java.time.LocalDateTime
import java.util.UUID

data class FineTuningJobResponse(
    val jobId: UUID,
    val status: String,
    val requestedAt: LocalDateTime
)