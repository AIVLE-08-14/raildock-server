package kr.co.raildock.raildock_server.feedback.dto

import java.util.UUID
import kr.co.raildock.raildock_server.common.enum.ModelType
import kr.co.raildock.raildock_server.feedback.enum.FeedbackStatus
import java.time.LocalDateTime

data class FeedbackCreateRequest(
    val problemId: UUID,
    val model: ModelType,
    val engineerId: Long,
    val sourceImageId: Long?,
    val boundingBoxJsonId: Long?
)

data class FeedbackResponse(
    val id: UUID,
    val problemId: UUID,
    val model: ModelType,
    val engineerId: Long,
    val feedbackStatus: FeedbackStatus,
    val sourceImageId: Long?,
    val boundingBoxJsonId: Long?,
    val createdTime: LocalDateTime
)

data class FeedbackUpdateRequest(
    val sourceImageId: Long?,
    val boundingBoxJsonId: Long?
)
