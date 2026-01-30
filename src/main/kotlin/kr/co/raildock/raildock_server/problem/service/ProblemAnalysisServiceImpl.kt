package kr.co.raildock.raildock_server.problem.analysis.service

import kr.co.raildock.raildock_server.problem.dto.ProblemSummaryDto
import kr.co.raildock.raildock_server.problem.enum.*
import kr.co.raildock.raildock_server.problem.entity.ProblemEntity
import kr.co.raildock.raildock_server.problem.repository.ProblemRepository
import kr.co.raildock.raildock_server.problem.service.ProblemAnalysisService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class ProblemAnalysisServiceImpl(
    private val problemRepository: ProblemRepository
) : ProblemAnalysisService {

    /* =========================
       공통 데이터 로딩
       - 날짜 범위 해석
       - railType / status 필터링
    ========================= */
    private fun loadProblems(
        from: LocalDate,
        to: LocalDate,
        railType: RailType?,
        status: ProblemStatus?
    ): List<ProblemEntity> {

        val fromDt = from.atStartOfDay()
        val toDt = to.plusDays(1).atStartOfDay()

        return problemRepository
            .findByDetectedTimeBetween(fromDt, toDt)
            .filter { railType == null || it.railType == railType }
            .filter { status == null || it.status == status }
    }

    /* =========================
       결함 타입별 집계
    ========================= */
    override fun countByType(
        from: LocalDate,
        to: LocalDate,
        railType: RailType?,
        status: ProblemStatus?
    ): List<CountByKeyDto> {

        return loadProblems(from, to, railType, status)
            .groupBy { it.problemType }
            .map { (type, list) ->
                CountByKeyDto(
                    key = type,
                    count = list.size.toLong()
                )
            }
            .sortedByDescending { it.count }
    }

    /* =========================
       구간(region)별 집계
    ========================= */
    override fun countByRegion(
        from: LocalDate,
        to: LocalDate,
        railType: RailType?
    ): List<CountByKeyDto> {

        return loadProblems(from, to, railType, null)
            .filter { it.region != null }
            .groupBy { it.region!! }
            .map { (region, list) ->
                CountByKeyDto(
                    key = region,
                    count = list.size.toLong()
                )
            }
            .sortedByDescending { it.count }
    }

    /* =========================
       결함 추이 (일 / 월 / 연)
    ========================= */
    override fun trend(
        from: LocalDate,
        to: LocalDate,
        unit: TimeUnit,
        railType: RailType?
    ): List<TrendPointDto> {

        val formatter = when (unit) {
            TimeUnit.DAY -> DateTimeFormatter.ofPattern("yyyy-MM-dd")
            TimeUnit.MONTH -> DateTimeFormatter.ofPattern("yyyy-MM")
            TimeUnit.YEAR -> DateTimeFormatter.ofPattern("yyyy")
        }

        return loadProblems(from, to, railType, null)
            .groupBy {
                when (unit) {
                    TimeUnit.DAY ->
                        it.detectedTime.toLocalDate()

                    TimeUnit.MONTH ->
                        it.detectedTime.toLocalDate().withDayOfMonth(1)

                    TimeUnit.YEAR ->
                        it.detectedTime.toLocalDate().withDayOfYear(1)
                }.format(formatter)
            }
            .map { (label, list) ->
                TrendPointDto(
                    label = label,
                    count = list.size.toLong()
                )
            }
            .sortedBy { it.label }
    }

    /* =========================
       최근 결함 TOP 10
    ========================= */
    override fun recentTop10(
        railType: RailType?,
        status: ProblemStatus?
    ): List<ProblemSummaryDto> {

        return problemRepository.findAllSummaries()
            .asSequence()
            .filter { railType == null || it.railType == railType }
            .filter { status == null || it.status == status }
            .sortedByDescending { it.detectedTime }
            .take(10)
            .toList()
    }
}
