package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.dto.FastAPIInferRequest
import kr.co.raildock.raildock_server.detect.dto.InferHealthResponse

interface FastApiClient {
    // TODO: Zip파일 그대로 쓸껀지...
    fun infer(req: FastAPIInferRequest): ByteArray
    fun inferHealthCheck(): InferHealthResponse
}