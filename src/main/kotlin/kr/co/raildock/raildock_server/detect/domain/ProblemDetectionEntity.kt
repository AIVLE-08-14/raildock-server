package kr.co.raildock.raildock_server.detect.domain

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import java.time.LocalDateTime

@Entity
@Table(name = "problem_detection")
class ProblemDetectionEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @CreatedDate
    var createdAt: LocalDateTime,
    @LastModifiedDate
    var updatedAt: LocalDateTime,

    var metadataFileId: Long? = null,

    var insulatorVideoFileId: Long? = null,
    var railVideoFileId: Long? = null,
    var nestVideoFileId: Long? = null,

    var videoTaskStatus: TaskStatus = TaskStatus.PENDING,
    var llmTaskStatus: TaskStatus = TaskStatus.CREATED,
    @Column(columnDefinition = "MEDIUMTEXT")
    var taskErrorMessage: String? = null,

    var videoDetectedZipFileId: Long? = null,

    var insulatorReportFileId: Long? = null,
    var railReportFileId: Long? = null,
    var nestReportFileId: Long? = null,

    var insulatorJsonId: Long? = null,
    var railJsonId: Long? = null,
    var nestJsonId: Long? = null,

    )