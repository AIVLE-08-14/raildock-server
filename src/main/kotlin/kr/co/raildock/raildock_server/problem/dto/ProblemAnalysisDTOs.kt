package kr.co.raildock.raildock_server.problem.dto

data class SystemProblemStatusSummaryDto(
    val unassignedCount: Long,
    val assignedCount: Long
)