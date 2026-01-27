package kr.co.raildock.raildock_server.detect.repository

import kr.co.raildock.raildock_server.detect.domain.DetectStatus
import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface ProblemDetectionRepository: JpaRepository<ProblemDetectionEntity, Long>{
    fun findAllByOrderByDatetimeDesc(pageable: Pageable): Page<ProblemDetectionEntity>
    fun findTopByTaskStatusOrderByDatetimeDesc(taskStatus: DetectStatus): ProblemDetectionEntity?

    @Modifying
    @Transactional
    @Query("""
        update ProblemDetectionEntity p
        set p.taskStatus = :to
        where p.id = :id and p.taskStatus = :from
    """)
    fun tryStart(
        @Param("id") id: Long,
        @Param("from") from: DetectStatus,
        @Param("to") to: DetectStatus
    ): Int

}
