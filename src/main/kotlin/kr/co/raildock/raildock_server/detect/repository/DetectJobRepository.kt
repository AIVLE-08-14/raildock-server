package kr.co.raildock.raildock_server.detect.repository

import kr.co.raildock.raildock_server.detect.domain.DetectJobEntity
import kr.co.raildock.raildock_server.detect.domain.DetectJobStatus
import org.springframework.data.jpa.repository.JpaRepository

interface DetectJobRepository: JpaRepository<DetectJobEntity, Long> {
    fun findTop10ByStatusOrderByCreatedAtAsc(status: DetectJobStatus): List<DetectJobEntity>
}