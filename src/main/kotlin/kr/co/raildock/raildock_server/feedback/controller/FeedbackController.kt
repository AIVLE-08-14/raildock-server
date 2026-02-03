package kr.co.raildock.raildock_server.feedback.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import java.util.UUID
import kr.co.raildock.raildock_server.feedback.dto.FeedbackCreateRequest
import kr.co.raildock.raildock_server.feedback.dto.FeedbackResponse
import kr.co.raildock.raildock_server.feedback.dto.FeedbackUpdateRequest
import kr.co.raildock.raildock_server.feedback.service.FeedbackService
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/feedbacks")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    @PostMapping(consumes = ["multipart/form-data"])
    fun create(
        @RequestPart("data") request: FeedbackCreateRequest,
        @RequestPart("jsonFile") jsonFile: MultipartFile
    ): FeedbackResponse =
        feedbackService.create(request, jsonFile)

    // TODO : Id 없이 전체 피드백 가져오는걸로 변경
    @GetMapping("/{id}")
    fun get(@PathVariable id: UUID): FeedbackResponse =
        feedbackService.get(id)

    @GetMapping("/problem/{problemId}")
    fun getByProblem(@PathVariable problemId: UUID): List<FeedbackResponse> =
        feedbackService.getByProblem(problemId)

    @PutMapping("/{id}")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: FeedbackUpdateRequest
    ): FeedbackResponse =
        feedbackService.update(id, request)

    @PostMapping("/{id}/complete")
    fun complete(@PathVariable id: UUID) {
        feedbackService.complete(id)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: UUID) {
        feedbackService.delete(id)
    }
}