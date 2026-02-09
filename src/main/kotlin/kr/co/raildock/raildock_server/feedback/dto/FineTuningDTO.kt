package kr.co.raildock.raildock_server.feedback.dto

import java.time.LocalDateTime

data class FineTuningJobResponse(
    val jobId: String,
    val status: String,
    val summary: Map<String, TaskSummaryDto>?,
    val config: FinetuneConfigDto?,
    val requestedAt: LocalDateTime
)

data class TaskSummaryDto(
    val pairs: Int,
    val copied: Int,
    val skipped: Int
)

data class FinetuneConfigDto(
    val epochs: Int,
    val batch: Int,
    val imgsz: Int
)
