package kr.co.raildock.raildock_server.problem.repository

import kr.co.raildock.raildock_server.problem.entity.ProblemEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import kr.co.raildock.raildock_server.problem.dto.ProblemSummaryDto
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.time.LocalDateTime

interface ProblemRepository : JpaRepository<ProblemEntity, UUID> {

    /**
     * 결함 목록 조회용 Summary DTO
     * - 필요한 컬럼만 SELECT
     * - 엔티티 생성 없음
     */
    @Query("""
        select new kr.co.raildock.raildock_server.problem.dto.ProblemSummaryDto(
            p.id,
            p.problemNum,
            p.problemType,
            p.severity,
            p.status,
            p.railType,
            p.latitude,
            p.longitude,
            p.detectedTime
        )
        from ProblemEntity p
    """)
    fun findAllSummaries(): List<ProblemSummaryDto>

    fun findByDetectedTimeBetween(
        from: LocalDateTime,
        to: LocalDateTime
    ): List<ProblemEntity>

}
