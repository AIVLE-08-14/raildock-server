package kr.co.raildock.raildock_server.detect.domain

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
class DetectJobEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var originalFilename: String,

    @Column(nullable = false, length = 1024)
    var videoPath: String,

    @Lob
    @Column(nullable = false)
    var metadataJson: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var status: DetectJobStatus = DetectJobStatus.PENDING,

    @Column(nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    var startedAt: OffsetDateTime? = null,
    var completedAt: OffsetDateTime? = null,

    @Column(length = 2000)
    var errorMessage: String? = null,
)