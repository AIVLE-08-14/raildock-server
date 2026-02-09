package kr.co.raildock.raildock_server.integration.vision

interface VisionClient {
    // TODO: Zip파일 그대로 쓸껀지...
    fun infer(req: VisionInferRequest): ByteArray
    fun inferHealthCheck(): VisionHealthResponse

    // Fine-tuning 관련
    fun feedbackUrl(req: FeedbackUrlRequest): FeedbackUrlResponse
    fun getFinetuneStatus(jobId: String): FinetuneStatusResponse
}