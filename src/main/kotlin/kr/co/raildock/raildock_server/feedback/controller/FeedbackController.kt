package kr.co.raildock.raildock_server.feedback.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.raildock.raildock_server.common.enum.ModelType
import org.springframework.web.bind.annotation.*
import java.util.UUID
import kr.co.raildock.raildock_server.feedback.dto.FeedbackCreateRequest
import kr.co.raildock.raildock_server.feedback.dto.FeedbackResponse
import kr.co.raildock.raildock_server.feedback.dto.FeedbackUpdateRequest
import kr.co.raildock.raildock_server.feedback.service.FeedbackService
import org.springframework.web.multipart.MultipartFile

@Tag(
    name = "feedback",
    description = "엔지니어 피드백 관리"
)
@RestController
@RequestMapping("/feedback")
class FeedbackController(
    private val feedbackService: FeedbackService
) {

    @PostMapping(consumes = ["multipart/form-data"])
    @Operation(summary = "피드백 작성 - problem ID와 Json파일 필요")
    fun create(
        @RequestParam problemId: UUID,
        @RequestParam jsonFile: MultipartFile
    ): FeedbackResponse =
        feedbackService.create(
            FeedbackCreateRequest(
                problemId = problemId
            ),
            jsonFile
        )

    @GetMapping
    @Operation(summary = "피드백 리스트 조회")
    fun getList(): List<FeedbackResponse> =
        feedbackService.getList()

    @GetMapping("/{id}")
    @Operation(summary = "피드백 id 조회")
    fun get(@PathVariable id: UUID): FeedbackResponse =
        feedbackService.get(id)

    @PatchMapping("/{id}")
    @Operation(summary = "피드백 Update")
    fun update(
        @PathVariable id: UUID,
        @RequestBody request: FeedbackUpdateRequest
    ): FeedbackResponse =
        feedbackService.update(id, request)

    @PatchMapping("/{id}/complete")
    @Operation(summary = "피드백 상태변경용")
    fun complete(@PathVariable id: UUID) {
        feedbackService.complete(id)
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "피드백 단건 삭제용")
    fun delete(@PathVariable id: UUID) {
        feedbackService.delete(id)
    }
}