package kr.co.raildock.raildock_server.problem.service

import kr.co.raildock.raildock_server.problem.dto.ProblemSummaryDto
import kr.co.raildock.raildock_server.problem.dto.SystemProblemStatusSummaryDto
import kr.co.raildock.raildock_server.problem.enum.*

interface ProblemDashboardService {
    // GIS용 전체문제 가져오기
    fun gisProblems(): List<ProblemSummaryDto>

    // 결함 요약
    fun statusSummary(): SystemProblemStatusSummaryDto

    // 최근 결함
    fun recentActiveProblems(): List<ProblemSummaryDto>

    // 이번 달 결함 타입 분포 - 빈도 기준 상위 N + ETC
    fun currentMonthCountByType(): List<CountByKeyDto>

    // 결함 발생 추이 - DAY   : 최근 30일 - MONTH : 최근 12개월 - YEAR  : 최근 10년
    fun problemCountTrend(unit: TimeUnit): List<TrendPointDto>
}
