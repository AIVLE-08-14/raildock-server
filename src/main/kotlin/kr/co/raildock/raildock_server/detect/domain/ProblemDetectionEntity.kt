package kr.co.raildock.raildock_server.detect.domain

import jakarta.persistence.*
import java.time.OffsetDateTime

@Entity
@Table(name = "problem_detection")
class ProblemDetectionEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var name: String,

    @Column(nullable = false)
    var section: String,

    @Column(nullable = false)
    var datetime: OffsetDateTime,

    @Column(nullable = false)
    var direction: String,

    var humidity: Int? = null,
    var temperature: Int? = null,
    var weather: String? = null,

    @Column(length = 1024)
    var metadataUrl: String? = null,

)