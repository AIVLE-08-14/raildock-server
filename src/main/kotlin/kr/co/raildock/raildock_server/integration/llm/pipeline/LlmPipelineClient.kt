package kr.co.raildock.raildock_server.integration.llm.pipeline

interface LlmPipelineClient {
    fun processVideo(
        videoUrl: String,
        originalMetadataUrl: String,
        generatePdf: Boolean = true,
        skipReview: Boolean = false
    ): ByteArray
}