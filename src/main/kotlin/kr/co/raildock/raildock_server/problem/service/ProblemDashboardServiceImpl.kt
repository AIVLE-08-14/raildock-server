package kr.co.raildock.raildock_server.problem.analysis.service

import kr.co.raildock.raildock_server.problem.dto.ProblemSummaryDto
import kr.co.raildock.raildock_server.problem.dto.SystemProblemStatusSummaryDto
import kr.co.raildock.raildock_server.problem.enum.*
import kr.co.raildock.raildock_server.problem.repository.ProblemRepository
import kr.co.raildock.raildock_server.problem.service.ProblemDashboardService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@Transactional(readOnly = true)
class ProblemDashboardServiceImpl(
    private val problemRepository: ProblemRepository
) : ProblemDashboardService {

    override fun gisProblems(): List<ProblemSummaryDto> {

        val activeStatuses = listOf(
            ProblemStatus.UNASSIGNED,
            ProblemStatus.ASSIGNED
        )

        return problemRepository
            .findByStatusIn(activeStatuses)
            .map { entity ->
                ProblemSummaryDto(
                    id = entity.id!!,
                    problemNum = entity.problemNum,
                    problemType = entity.problemType,
                    severity = entity.severity,
                    status = entity.status,
                    railType = entity.railType,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    detectedTime = entity.detectedTime
                )
            }
    }

    override fun statusSummary(): SystemProblemStatusSummaryDto {

        val unassigned = problemRepository.countByStatus(
            ProblemStatus.UNASSIGNED
        )

        val assigned = problemRepository.countByStatus(
            ProblemStatus.ASSIGNED
        )

        return SystemProblemStatusSummaryDto(
            unassignedCount = unassigned,
            assignedCount = assigned
        )
    }

    override fun recentActiveProblems(): List<ProblemSummaryDto> {

        val activeStatuses = listOf(
            ProblemStatus.UNASSIGNED,
            ProblemStatus.ASSIGNED
        )

        return problemRepository
            .findTop10ByStatusInOrderByDetectedTimeDesc(activeStatuses)
            .map { entity ->
                ProblemSummaryDto(
                    id = entity.id!!,
                    problemNum = entity.problemNum,
                    problemType = entity.problemType,
                    severity = entity.severity,
                    status = entity.status,
                    railType = entity.railType,
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    detectedTime = entity.detectedTime
                )
            }
    }

    override fun currentMonthCountByType(): List<CountByKeyDto> {

        val now = LocalDate.now()
        val from = now.withDayOfMonth(1).atStartOfDay()
        val to = now.withDayOfMonth(now.lengthOfMonth())
            .plusDays(1)
            .atStartOfDay()

        return problemRepository
            .findByDetectedTimeBetween(from, to)
            .groupBy { it.problemType }
            .map { (type, list) ->
                CountByKeyDto(
                    key = type,
                    count = list.size.toLong()
                )
            }
            .sortedByDescending { it.count }
    }

    override fun problemCountTrend(unit: TimeUnit): List<TrendPointDto> {

        val now = LocalDate.now()

        val (from, labels, formatter) = when (unit) {

            TimeUnit.DAY -> {
                val start = now.minusDays(29)
                Triple(
                    start,
                    (0..29).map { start.plusDays(it.toLong()) },
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                )
            }

            TimeUnit.MONTH -> {
                val start = now.minusMonths(11).withDayOfMonth(1)
                Triple(
                    start,
                    (0..11).map { start.plusMonths(it.toLong()) },
                    DateTimeFormatter.ofPattern("yyyy-MM")
                )
            }

            TimeUnit.YEAR -> {
                val start = now.minusYears(9).withDayOfYear(1)
                Triple(
                    start,
                    (0..9).map { start.plusYears(it.toLong()) },
                    DateTimeFormatter.ofPattern("yyyy")
                )
            }
        }

        val fromDt = from.atStartOfDay()
        val toDt = now.plusDays(1).atStartOfDay()

        val grouped = problemRepository
            .findByDetectedTimeBetween(fromDt, toDt)
            .groupBy {
                when (unit) {
                    TimeUnit.DAY ->
                        it.detectedTime.toLocalDate()

                    TimeUnit.MONTH ->
                        it.detectedTime.toLocalDate().withDayOfMonth(1)

                    TimeUnit.YEAR ->
                        it.detectedTime.toLocalDate().withDayOfYear(1)
                }
            }
            .mapValues { it.value.size.toLong() }

        return labels.map { date ->
            TrendPointDto(
                label = date.format(formatter),
                count = grouped[date] ?: 0L
            )
        }
    }
}
