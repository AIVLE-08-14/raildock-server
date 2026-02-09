package kr.co.raildock.raildock_server.integration.llm.chatbot

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kr.co.raildock.raildock_server.common.ApiResponse
import kr.co.raildock.raildock_server.user.dto.SignUpRequestDTO
import kr.co.raildock.raildock_server.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "Chatbot",
    description = "Chatbot과 상호작용 API"
)
@RestController
@RequestMapping("/api/chatbot")
class ChatbotController(
    private val chatbotService: ChatbotService
) {

    @PostMapping("/ask")
    @Operation(summary = "RAILDOCK에게 질문하기")
    fun ask(
        @Valid @RequestBody request: ChatbotAskRequest
    ): ResponseEntity<ApiResponse<ChatbotAskResponse>> {

        val response = chatbotService.ask(request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
