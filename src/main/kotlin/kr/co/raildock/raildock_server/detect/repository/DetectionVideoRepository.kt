package kr.co.raildock.raildock_server.detect.repository

import kr.co.raildock.raildock_server.detect.domain.DetectJobStatus
import kr.co.raildock.raildock_server.detect.domain.DetectionVideoEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface DetectionVideoRepository: JpaRepository<DetectionVideoEntity, Long>{
    fun findTop10ByTaskStatusOrderByCreatedAtAsc(taskStatus: DetectJobStatus): List<DetectionVideoEntity>

    @Modifying
    @Transactional
    @Query("""
        update DetectionVideoEntity v
        set v.taskStatus = :running
        where v.id = :id
        and v.taskStatus = :pending
    """)
    fun tryStart(
        @Param("id") id: Long,
        @Param("pending") pending: DetectJobStatus = DetectJobStatus.PENDING,
        @Param("running") running: DetectJobStatus = DetectJobStatus.RUNNING
    ): Int

    fun findAllByProblemDetectionId(problemDetectionId: Long): List<DetectionVideoEntity>
    fun findAllByProblemDetectionIdIn(problemDetectionIds: List<Long>): List<DetectionVideoEntity>
}