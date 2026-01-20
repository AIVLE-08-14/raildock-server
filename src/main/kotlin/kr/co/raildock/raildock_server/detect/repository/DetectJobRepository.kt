package kr.co.raildock.raildock_server.detect.repository

import kr.co.raildock.raildock_server.detect.domain.DetectJobEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DetectJobRepository: JpaRepository<DetectJobEntity, Long>