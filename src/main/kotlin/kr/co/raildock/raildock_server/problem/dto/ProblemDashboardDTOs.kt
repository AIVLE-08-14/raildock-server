package kr.co.raildock.raildock_server.problem.dto

data class SystemProblemStatusSummaryDto(
    val unassignedCount: Long,
    val assignedCount: Long
)

data class CountByKeyDto(
    val key: String,
    val count: Long
)

data class TrendPointDto(
    val label: String,   // 날짜 / 월 / 연 (x축)
    val count: Long      // 결함 개수 (y축)
)