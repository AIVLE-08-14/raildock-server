package kr.co.raildock.raildock_server.integration.llm.chatbot

import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ChatbotClientImpl(
    private val llmWebClient: WebClient
) : ChatbotClient {

    override fun ask(request: ChatbotAskRequest): ChatbotAskResponse {
        return llmWebClient.post()
            .uri("/chatbot/ask")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatbotAskResponse::class.java)
            .block()!!   // 👉 동기 방식 (서버 내부용)
    }
}
