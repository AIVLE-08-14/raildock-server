package kr.co.raildock.raildock_server.detect.domain

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(
    name = "detection_video",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_pd_video_type", columnNames = ["problem_detection_id", "video_type"])
    ])
class DetectionVideoEntity (
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_detection_id", nullable = false)
    var problemDetection: ProblemDetectionEntity,

    @Enumerated(EnumType.STRING)
    @Column(name = "video_type", nullable = false, length = 20)
    var videoType: VideoType,

    @Column(nullable = false, length = 1024)
    var videoURL: String,

    @Column(nullable = false)
    var originalFilename: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    var taskStatus: DetectJobStatus = DetectJobStatus.PENDING,

    @Column(nullable = false)
    var createdAt: OffsetDateTime = OffsetDateTime.now(),

    var updatedAt: OffsetDateTime? = null,
    var completedAt: OffsetDateTime? = null,

    @Column(length = 2000)
    var errorMessage: String? = null,
    )

