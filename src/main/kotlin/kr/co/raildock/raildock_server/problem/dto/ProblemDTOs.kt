package kr.co.raildock.raildock_server.problem.dto

import java.time.LocalDateTime
import java.util.UUID
import kr.co.raildock.raildock_server.problem.enum.*

data class ProblemSummaryDto(
    val id: UUID,
    val problemNum: String,
    val problemType: String,
    val severity: Severity,
    val status: ProblemStatus,
    val railType: RailType,
    val detectedTime: LocalDateTime
)


data class ProblemDetailDto(

    val id: UUID,
    val detectionId: UUID,
    val problemNum: String,

    val status: ProblemStatus,
    val severity: Severity,
    val severityReason: String?,
    val reference: String?,

    val problemType: String,
    val railType: RailType,
    val component: String,

    val latitude: Double,
    val longitude: Double,
    val region: String?,

    val weather: String?,
    val temperature: Int?,
    val humidity: Int?,

    val detectedTime: LocalDateTime,

    val managerId: UUID?,

    val sourceImageId: UUID,
    val boundingBoxJsonId: UUID
)


data class ProblemCreateRequest(

    val detectionId: UUID,
    val problemNum: String,

    val problemType: String,
    val railType: RailType,
    val component: String,

    val severity: Severity,
    val severityReason: String?,

    val latitude: Double,
    val longitude: Double,
    val region: String?,

    val weather: String?,
    val temperature: Int?,
    val humidity: Int?,

    val detectedTime: LocalDateTime,

    val sourceImageId: UUID,
    val boundingBoxJsonId: UUID
)


/**
 * 문제 처리 상태 변경용
 * (워크플로우 제어 목적)
 */
data class ProblemStatusUpdateRequest(
    val status: ProblemStatus
)

/**
 * 문제 내용 수정용
 * (관리자 판단/보정 목적)
 */
data class ProblemContentUpdateRequest(

    val severity: Severity?,
    val severityReason: String?,
    val reference: String?,

    val managerId: UUID?,

    val problemType: String?,
    val component: String?,

    val railType: RailType?,
    val region: String?,

    val weather: String?,
    val temperature: Int?,
    val humidity: Int?
)
