package kr.co.raildock.raildock_server.feedback.entity

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID
import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.feedback.enum.FeedbackStatus

@Entity
@Table(name = "feedback")
class FeedbackEntity(

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)")
    val id: UUID? = null,

    @Column(name = "problem_id", nullable = false, columnDefinition = "BINARY(16)")
    val problemId: UUID,

    @Enumerated(EnumType.STRING)
    @Column(name = "model", nullable = false, length = 30)
    val model: ModelType,

    @Column(name = "created_time", nullable = false)
    var createdTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "engineer_id", nullable = false)
    var engineerId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "feedback_status")
    var feedbackStatus: FeedbackStatus,

    @Column(name = "source_image_id")
    var sourceImageId: Long? = null,

    @Column(name = "bounding_box_json_id")
    var boundingBoxJsonId: Long? = null
)
