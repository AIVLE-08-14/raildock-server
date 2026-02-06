package kr.co.raildock.raildock_server.integration.llm

import kr.co.raildock.raildock_server.integration.llm.chatbot.ChatbotClient
import kr.co.raildock.raildock_server.integration.llm.pipeline.LlmPipelineClient

interface LlmClient {
    val pipeline: LlmPipelineClient
    val chatbot: ChatbotClient
    // TODO: Document
}