package kr.co.raildock.raildock_server.detect.repository

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemDetectionRepository: JpaRepository<ProblemDetectionEntity, Long>{
    fun findAllByOrderByDatetimeDesc(pageable: Pageable): Page<ProblemDetectionEntity>
}
