package kr.co.raildock.raildock_server.problem.enum

data class CountByKeyDto(
    val key: String,
    val count: Long
)

data class TrendPointDto(
    val label: String,   // "2026-01-01", "2026-01", "2026"
    val count: Long
)

enum class TimeUnit {
    DAY,
    MONTH,
    YEAR
}