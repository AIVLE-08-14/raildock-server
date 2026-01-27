package kr.co.raildock.raildock_server.problem.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID
import kr.co.raildock.raildock_server.problem.enum.*

@Entity
@Table(name = "problems")
class ProblemEntity(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false)
    var createdTime: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var problemType: ProblemType,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var severity: Severity,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ProblemStatus = ProblemStatus.UNASSIGNED,

    @Column(nullable = false)
    var latitude: Double,

    @Column(nullable = false)
    var longitude: Double,

    @Column(nullable = true)
    var managerId: UUID? = null,

    @Column(nullable = false)
    var originalImageId: UUID,

    @Column(nullable = false)
    var bboxJsonId: UUID,

    @Column(nullable = false)
    var reportId: UUID
)
