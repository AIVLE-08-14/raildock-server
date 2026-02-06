package kr.co.raildock.raildock_server.problem.dto

import kr.co.raildock.raildock_server.common.enum.ModelType
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
    val latitude: Double,
    val longitude: Double,
    val detectedTime: LocalDateTime
)


data class ProblemDetailDto(

    val id: UUID,
    val detectionId: Long,
    val model: ModelType,
    val problemNum: String,

    val status: ProblemStatus,
    val severity: Severity,
    val severityReason: String?,
    val reference: String?,
    val recommendedActions : String?,

    val problemType: String,
    val problemStatus: String,
    val railType: RailType,
    val component: String,

    val latitude: Double,
    val longitude: Double,
    val region: String?,

    val weather: String?,
    val temperature: Int?,
    val humidity: Int?,

    val detectedTime: LocalDateTime,

    val managerId: Long?,

    val sourceImageIdURL: String?,
    val boundingBoxJsonIdURL: String?
)


data class ProblemCreateRequest(

    val detectionId: Long,
    val problemNum: String,
    val model: ModelType,

    val problemType: String,
    val problemStatus: String,
    val railType: RailType,
    val component: String,
    val reference: String?,

    val severity: Severity,
    val severityReason: String?,
    val recommendedActions : String,

    val latitude: Double,
    val longitude: Double,
    val region: String?,

    val weather: String?,
    val temperature: Int?,
    val humidity: Int?,

    val detectedTime: LocalDateTime,

    val sourceImageId: Long,
    val boundingBoxJsonId: Long
)

/**
 * 문제 내용 수정용
 * (관리자 판단/보정 목적)
 */
data class ProblemContentUpdateRequest(
    val severity: Severity?,
    val severityReason: String?,
    val model: ModelType?,
    val reference: String?,
    val recommendedActions: String?,

    val managerId: Long?,

    val problemType: String?,
    val problemStatus: String,
    val component: String?,

    val railType: RailType?,
    val region: String?,

    val weather: String?,
    val temperature: Int?,
    val humidity: Int?
)

/**
 * 결함 담당자 변경 요청
 */
data class ProblemManagerUpdateRequest(
    val managerId: Long?
)