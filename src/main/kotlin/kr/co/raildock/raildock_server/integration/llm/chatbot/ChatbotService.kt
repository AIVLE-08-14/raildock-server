package kr.co.raildock.raildock_server.integration.llm.chatbot


import kr.co.raildock.raildock_server.integration.llm.LlmClient
import org.springframework.stereotype.Service

@Service
class ChatbotService(
    private val llmClient: LlmClient
) {
    fun ask(request: ChatbotAskRequest): ChatbotAskResponse {
        return llmClient.chatbot.ask(request)
    }
}
