package kr.co.raildock.raildock_server.detect.dto

import java.time.OffsetDateTime

data class DetectJobGetResponse(
    val jobId: Long,
    val status: String,
    val createdAt: OffsetDateTime,
    val startedAt: OffsetDateTime?,
    val completedAt: OffsetDateTime?,
    val errorMessage: String?,
)
