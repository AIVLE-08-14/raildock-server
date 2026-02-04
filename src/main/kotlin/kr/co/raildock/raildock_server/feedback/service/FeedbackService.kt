package kr.co.raildock.raildock_server.feedback.service

import java.util.UUID
import kr.co.raildock.raildock_server.feedback.dto.FeedbackCreateRequest
import kr.co.raildock.raildock_server.feedback.dto.FeedbackResponse
import kr.co.raildock.raildock_server.feedback.dto.FeedbackUpdateRequest
import org.springframework.web.multipart.MultipartFile

interface FeedbackService {

    fun create(
        request: FeedbackCreateRequest,
        jsonFile: MultipartFile
    ): FeedbackResponse

    fun getList(): List<FeedbackResponse>

    fun get(feedbackId: UUID): FeedbackResponse

    fun update(feedbackId: UUID, request: FeedbackUpdateRequest): FeedbackResponse

    fun complete(feedbackId: UUID)

    fun delete(feedbackId: UUID)
}

