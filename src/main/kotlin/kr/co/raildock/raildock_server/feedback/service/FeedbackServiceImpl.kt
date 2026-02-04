package kr.co.raildock.raildock_server.feedback.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID
import kr.co.raildock.raildock_server.feedback.dto.*
import kr.co.raildock.raildock_server.feedback.entity.FeedbackEntity
import kr.co.raildock.raildock_server.feedback.enum.FeedbackStatus
import kr.co.raildock.raildock_server.feedback.repository.FeedbackRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.problem.service.ProblemService
import org.springframework.web.multipart.MultipartFile

@Service
@Transactional
class FeedbackServiceImpl(
    private val feedbackRepository: FeedbackRepository,
    private val fileService: FileService,
    private val problemService: ProblemService
) : FeedbackService {

    override fun create(
        request: FeedbackCreateRequest,
        jsonFile: MultipartFile
    ): FeedbackResponse {

        val uploadedJson = fileService.upload(
            file = jsonFile,
            fileType = FileType.JSON
        )
        val jsonFileId = uploadedJson.fileId

        problemService.updateBoundingBoxJson(
            problemId = request.problemId,
            jsonFileId = jsonFileId
        )

        val feedback = feedbackRepository.save(
            FeedbackEntity(
                problemId = request.problemId,
                model = request.model,
                engineerId = request.engineerId,
                feedbackStatus = FeedbackStatus.PENDING,
                boundingBoxJsonId = jsonFileId
            )
        )

        return FeedbackResponse(
            id = feedback.id!!,
            problemId = feedback.problemId,
            model = feedback.model,
            engineerId = feedback.engineerId,
            feedbackStatus = feedback.feedbackStatus,
            sourceImageId = feedback.sourceImageId,
            boundingBoxJsonId = feedback.boundingBoxJsonId,
            createdTime = feedback.createdTime
            // 🔜 여기서 URL 생성 가능
        )
    }

    @Transactional(readOnly = true)
    override fun getList(): List<FeedbackResponse> =
        feedbackRepository.findAll()
            .map { feedback ->
                FeedbackResponse(
                    id = feedback.id!!,
                    problemId = feedback.problemId,
                    model = feedback.model,
                    engineerId = feedback.engineerId,
                    feedbackStatus = feedback.feedbackStatus,
                    sourceImageId = feedback.sourceImageId,
                    boundingBoxJsonId = feedback.boundingBoxJsonId,
                    createdTime = feedback.createdTime
                )
            }

    @Transactional(readOnly = true)
    override fun get(feedbackId: UUID): FeedbackResponse {
        val feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow { IllegalArgumentException("Feedback not found") }

        return FeedbackResponse(
            id = feedback.id!!,
            problemId = feedback.problemId,
            model = feedback.model,
            engineerId = feedback.engineerId,
            feedbackStatus = feedback.feedbackStatus,
            sourceImageId = feedback.sourceImageId,
            boundingBoxJsonId = feedback.boundingBoxJsonId,
            createdTime = feedback.createdTime
        )
    }

    override fun update(
        feedbackId: UUID,
        request: FeedbackUpdateRequest
    ): FeedbackResponse {
        val feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow { IllegalArgumentException("Feedback not found") }

        feedback.sourceImageId = request.sourceImageId
        feedback.boundingBoxJsonId = request.boundingBoxJsonId

        return FeedbackResponse(
            id = feedback.id!!,
            problemId = feedback.problemId,
            model = feedback.model,
            engineerId = feedback.engineerId,
            feedbackStatus = feedback.feedbackStatus,
            sourceImageId = feedback.sourceImageId,
            boundingBoxJsonId = feedback.boundingBoxJsonId,
            createdTime = feedback.createdTime
        )
    }

    override fun complete(feedbackId: UUID) {
        val feedback = feedbackRepository.findById(feedbackId)
            .orElseThrow { IllegalArgumentException("Feedback not found") }

        if (feedback.feedbackStatus == FeedbackStatus.COMPLETE) return

        feedback.feedbackStatus = FeedbackStatus.COMPLETE
    }

    override fun delete(feedbackId: UUID) {
        if (!feedbackRepository.existsById(feedbackId)) {
            throw IllegalArgumentException("Feedback not found")
        }
        feedbackRepository.deleteById(feedbackId)
    }
}
