package kr.co.raildock.raildock_server.problem.controller

import kr.co.raildock.raildock_server.problem.dto.ProblemSummaryDto
import kr.co.raildock.raildock_server.problem.enum.*
import kr.co.raildock.raildock_server.problem.service.ProblemAnalysisService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/problems/analysis")
class ProblemAnalysisController(
    private val problemAnalysisService: ProblemAnalysisService
) {

    /* =========================
       결함 타입별 집계
    ========================= */
    @GetMapping("/by-type")
    fun countByType(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam(required = false) railType: RailType?,
        @RequestParam(required = false) status: ProblemStatus?
    ): ResponseEntity<List<CountByKeyDto>> =
        ResponseEntity.ok(
            problemAnalysisService.countByType(from, to, railType, status)
        )

    /* =========================
       구간(region)별 집계
    ========================= */
    @GetMapping("/by-region")
    fun countByRegion(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam(required = false) railType: RailType?
    ): ResponseEntity<List<CountByKeyDto>> =
        ResponseEntity.ok(
            problemAnalysisService.countByRegion(from, to, railType)
        )

    /* =========================
       결함 추이 (일/월/연)
    ========================= */
    @GetMapping("/trend")
    fun trend(
        @RequestParam from: LocalDate,
        @RequestParam to: LocalDate,
        @RequestParam unit: TimeUnit, // DAY | MONTH | YEAR
        @RequestParam(required = false) railType: RailType?
    ): ResponseEntity<List<TrendPointDto>> =
        ResponseEntity.ok(
            problemAnalysisService.trend(from, to, unit, railType)
        )

    /* =========================
       최근 결함 TOP 10
    ========================= */
    @GetMapping("/recent-top10")
    fun recentTop10(
        @RequestParam(required = false) railType: RailType?,
        @RequestParam(required = false) status: ProblemStatus?
    ): ResponseEntity<List<ProblemSummaryDto>> =
        ResponseEntity.ok(
            problemAnalysisService.recentTop10(railType, status)
        )
}
