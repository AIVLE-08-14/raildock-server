package kr.co.raildock.raildock_server.detect.repository

import kr.co.raildock.raildock_server.detect.domain.TaskStatus
import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface ProblemDetectionRepository: JpaRepository<ProblemDetectionEntity, Long>{
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<ProblemDetectionEntity>
    fun findTopByVideoTaskStatusOrderByCreatedAtDesc(videoTaskStatus: TaskStatus): ProblemDetectionEntity?
    fun findTopByLlmTaskStatusOrderByCreatedAtDesc(lmTaskStatus: TaskStatus): ProblemDetectionEntity?

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(
        """
        update ProblemDetectionEntity p
        set p.videoTaskStatus = :to
        where p.id = :id and p.videoTaskStatus = :from
    """
    )
    fun tryStartVideo(
        @Param("id") id: Long,
        @Param("from") from: TaskStatus,
        @Param("to") to: TaskStatus
    ): Int

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query(
        """
        update ProblemDetectionEntity p
        set p.llmTaskStatus = :to
        where p.id = :id and p.llmTaskStatus = :from
    """
    )
    fun tryStartLlm(
        @Param("id") id: Long,
        @Param("from") from: TaskStatus,
        @Param("to") to: TaskStatus
    ): Int

}
