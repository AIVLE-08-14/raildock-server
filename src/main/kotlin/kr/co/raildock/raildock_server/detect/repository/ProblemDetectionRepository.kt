package kr.co.raildock.raildock_server.detect.repository

import kr.co.raildock.raildock_server.detect.domain.ProblemDetectionEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ProblemDetectionRepository: JpaRepository<ProblemDetectionEntity, Long>