package kr.co.raildock.raildock_server.integration.llm

import kr.co.raildock.raildock_server.integration.llm.pipeline.LlmPipelineClient

interface LlmClient {
    val pipeline: LlmPipelineClient
    // TODO: Chat
    // TODO: Document
}