package kr.co.raildock.raildock_server.feedback.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kr.co.raildock.raildock_server.feedback.dto.*
import kr.co.raildock.raildock_server.feedback.entity.FeedbackEntity
import kr.co.raildock.raildock_server.feedback.enum.FeedbackStatus
import kr.co.raildock.raildock_server.feedback.repository.FeedbackRepository

@Service
@Transactional
class FeedbackServiceImpl(
    private val feedbackRepository: FeedbackRepository
) : FeedbackService {

    override fun create(request: FeedbackCreateRequest): FeedbackResponse {
        val entity = feedbackRepository.save(
            FeedbackEntity(
                problemId = request.problemId,
                model = request.model,
                engineerId = request.engineerId,
                feedbackStatus = FeedbackStatus.PENDING,
                sourceImageId = request.sourceImageId,
                boundingBoxJsonId = request.boundingBoxJsonId
            )
        )
        return entity.toResponse()
    }

    @Transactional(readOnly = true)
    override fun get(feedbackId: UUID): FeedbackResponse =
        feedbackRepository.findById(feedbackId)
            .orElseThrow { IllegalArgumentException("Feedback not found") }
            .toResponse()

    @Transactional(readOnly = true)
    override fun getByProblem(problemId: UUID): List<FeedbackResponse> =
        feedbackRepository.findAllByProblemId(problemId)
            .map { it.toResponse() }

    override fun update(
        feedbackId: UUID,
        request: FeedbackUpdateRequest
    ): FeedbackResponse {
        val origin = feedbackRepository.findById(feedbackId)
            .orElseThrow { IllegalArgumentException("Feedback not found") }

        val updated = origin.copy(
            sourceImageId = request.sourceImageId,
            boundingBoxJsonId = request.boundingBoxJsonId
        )

        return feedbackRepository.save(updated).toResponse()
    }

    override fun complete(feedbackId: UUID) {
        val origin = feedbackRepository.findById(feedbackId)
            .orElseThrow { IllegalArgumentException("Feedback not found") }

        if (origin.feedbackStatus == FeedbackStatus.COMPLETE) return

        val completed = origin.copy(
            feedbackStatus = FeedbackStatus.COMPLETE
        )

        feedbackRepository.save(completed)
    }

    override fun delete(feedbackId: UUID) {
        if (!feedbackRepository.existsById(feedbackId)) {
            throw IllegalArgumentException("Feedback not found")
        }
        feedbackRepository.deleteById(feedbackId)
    }

    private fun FeedbackEntity.toResponse(): FeedbackResponse =
        FeedbackResponse(
            id = this.id!!,
            problemId = this.problemId,
            model = this.model,
            engineerId = this.engineerId,
            feedbackStatus = this.feedbackStatus,
            sourceImageId = this.sourceImageId,
            boundingBoxJsonId = this.boundingBoxJsonId,
            createdTime = this.createdTime
        )

    /** 불변 엔티티용 copy */
    private fun FeedbackEntity.copy(
        sourceImageId: Long? = this.sourceImageId,
        boundingBoxJsonId: Long? = this.boundingBoxJsonId,
        feedbackStatus: FeedbackStatus = this.feedbackStatus
    ): FeedbackEntity =
        FeedbackEntity(
            id = this.id,
            problemId = this.problemId,
            model = this.model,
            engineerId = this.engineerId,
            createdTime = this.createdTime,
            feedbackStatus = feedbackStatus,
            sourceImageId = sourceImageId,
            boundingBoxJsonId = boundingBoxJsonId
        )
}
