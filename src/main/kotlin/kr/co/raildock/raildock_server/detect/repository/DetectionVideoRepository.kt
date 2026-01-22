package kr.co.raildock.raildock_server.detect.repository

import kr.co.raildock.raildock_server.detect.domain.DetectionVideoEntity
import org.springframework.data.jpa.repository.JpaRepository

interface DetectionVideoRepository: JpaRepository<DetectionVideoEntity, Long>