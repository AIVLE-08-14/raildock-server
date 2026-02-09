package kr.co.raildock.raildock_server.integration.llm.chatbot

interface ChatbotClient {
    fun ask(request: ChatbotAskRequest): ChatbotAskResponse
}