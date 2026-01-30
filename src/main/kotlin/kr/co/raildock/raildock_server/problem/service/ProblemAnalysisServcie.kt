package kr.co.raildock.raildock_server.problem.service

import kr.co.raildock.raildock_server.problem.dto.ProblemSummaryDto
import kr.co.raildock.raildock_server.problem.enum.*
import java.time.LocalDate

interface ProblemAnalysisService {

    /* =========================
       결함 타입별 집계
    ========================= */
    fun countByType(
        from: LocalDate,
        to: LocalDate,
        railType: RailType?,
        status: ProblemStatus?
    ): List<CountByKeyDto>

    /* =========================
       구간(region)별 집계
    ========================= */
    fun countByRegion(
        from: LocalDate,
        to: LocalDate,
        railType: RailType?
    ): List<CountByKeyDto>

    /* =========================
       결함 추이 (일/월/연)
    ========================= */
    fun trend(
        from: LocalDate,
        to: LocalDate,
        unit: TimeUnit,
        railType: RailType?
    ): List<TrendPointDto>

    /* =========================
       최근 결함 TOP 10
    ========================= */
    fun recentTop10(
        railType: RailType?,
        status: ProblemStatus?
    ): List<ProblemSummaryDto>
}
