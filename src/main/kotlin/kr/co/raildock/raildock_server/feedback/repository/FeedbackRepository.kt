package kr.co.raildock.raildock_server.feedback.repository

import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID
import kr.co.raildock.raildock_server.feedback.entity.FeedbackEntity
import kr.co.raildock.raildock_server.feedback.enum.FeedbackStatus

interface FeedbackRepository : JpaRepository<FeedbackEntity, UUID> {

    fun findAllByProblemId(problemId: UUID): List<FeedbackEntity>

    fun findAllByEngineerId(engineerId: Long): List<FeedbackEntity>

    fun findAllByFeedbackStatus(status: FeedbackStatus): List<FeedbackEntity>
}
