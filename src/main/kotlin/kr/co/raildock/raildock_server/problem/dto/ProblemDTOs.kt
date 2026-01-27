package kr.co.raildock.raildock_server.problem.dto

import java.time.LocalDateTime
import java.util.UUID
import kr.co.raildock.raildock_server.problem.enum.*

data class ProblemSummaryDto(
    val id: UUID,
    val problemType: ProblemType,
    val severity: Severity,
    val status: ProblemStatus,
    val createdTime: LocalDateTime
)

data class ProblemDetailDto(
    val id: UUID,
    val createdTime: LocalDateTime,
    val problemType: ProblemType,
    val severity: Severity,
    val status: ProblemStatus,
    val latitude: Double,
    val longitude: Double,
    val managerId: UUID?,
    val originalImageId: UUID,
    val bboxJsonId: UUID,
    val reportId: UUID
)

data class ProblemCreateRequest(
    val problemType: ProblemType,
    val severity: Severity,
    val latitude: Double,
    val longitude: Double,
    val originalImageId: UUID,
    val bboxJsonId: UUID,
    val reportId: UUID
)

data class ProblemUpdateRequest(
    val severity: Severity?,
    val status: ProblemStatus?,
    val managerId: UUID?
)
