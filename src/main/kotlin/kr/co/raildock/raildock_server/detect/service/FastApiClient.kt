package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.dto.InferRequest
import kr.co.raildock.raildock_server.detect.dto.InferResponse

interface FastApiClient {
    fun infer(req: InferRequest): InferResponse
}