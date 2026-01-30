package kr.co.raildock.raildock_server.integration.llm.pipeline

import kr.co.raildock.raildock_server.integration.llm.dto.ProcessVideoResponse

interface LlmPipelineClient {
    fun processVideo(
        videoUrl: String,
        originalMetadataUrl: String,
        generatePdf: Boolean = true,
        skipReview: Boolean = false
    ): ProcessVideoResponse
}