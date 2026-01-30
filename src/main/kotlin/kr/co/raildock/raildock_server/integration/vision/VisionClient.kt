package kr.co.raildock.raildock_server.integration.vision

interface VisionClient {
    // TODO: Zip파일 그대로 쓸껀지...
    fun infer(req: VisionInferRequest): ByteArray
    fun inferHealthCheck(): VisionHealthResponse
}