package kr.co.raildock.raildock_server.integration.llm

import kr.co.raildock.raildock_server.integration.llm.chatbot.ChatbotClient
import kr.co.raildock.raildock_server.integration.llm.pipeline.LlmPipelineClient
import org.springframework.stereotype.Component

@Component
class LlmClientImpl(
    override val pipeline: LlmPipelineClient,
    override val chatbot: ChatbotClient
): LlmClient