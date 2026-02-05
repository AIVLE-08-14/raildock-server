package kr.co.raildock.raildock_server.problem.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.raildock.raildock_server.problem.dto.ProblemSummaryDto
import kr.co.raildock.raildock_server.problem.enum.*
import kr.co.raildock.raildock_server.problem.service.ProblemDashboardService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Dashboard-Problem",
    description = "메인화면 dashboard용 Problem 호출 API"
)
@RestController
@RequestMapping("/api/dashboard/problem")
class ProblemDashboardController(
    private val problemDashboardService: ProblemDashboardService
) {
    @GetMapping("/GIS")
    @Operation(summary = "GIS용 문제(결함) 전체 간단조회")
    fun list(): ResponseEntity<List<ProblemSummaryDto>> =
        ResponseEntity.ok(problemDashboardService.gisProblems())

    @GetMapping("/status")
    @Operation(summary = "시스템 결함상태 요약 (UNASSIGNED/ASSIGNED)")
    fun statusSummary() =
        ResponseEntity.ok(
            problemDashboardService.statusSummary()
        )
    
    @GetMapping("/recent")
    @Operation(summary = "최근 결함 목록 10가지 (UNASSIGNED/ASSIGNED)")
    fun recentActiveProblems(): ResponseEntity<List<ProblemSummaryDto>> =
        ResponseEntity.ok(
            problemDashboardService.recentActiveProblems()
        )

    @GetMapping("/current/by-type")
    @Operation(summary = "이번달 결합 타입 분포 (막대그래프 용)")
    fun currentMonthByType(): ResponseEntity<List<CountByKeyDto>> =
        ResponseEntity.ok(
            problemDashboardService.currentMonthCountByType()
        )

    @GetMapping("/trend/count")
    @Operation(summary = "결함 카운트 추이 (일간 - 30일, 월간 - 12개월, 연간 - 10년 )")
    fun problemCountTrend(
        @RequestParam unit: TimeUnit
    ): ResponseEntity<List<TrendPointDto>> =
        ResponseEntity.ok(
            problemDashboardService.problemCountTrend(unit)
        )
}
