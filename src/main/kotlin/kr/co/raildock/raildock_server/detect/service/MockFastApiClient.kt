package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.dto.InferRequest
import kr.co.raildock.raildock_server.detect.dto.InferResponse
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import kotlin.random.Random

@Profile("mock")
@Component
class MockFastApiClient : FastApiClient {

    override fun infer(req: InferRequest): InferResponse {
        // "추론 시간" 흉내
        Thread.sleep(Random.nextLong(300, 1200))

        // 결과도 대충 만들어주기 (나중에 defect 저장 붙이기 쉬움)
        val defects = listOf(
            mapOf(
                "type" to "crack",
                "confidence" to 0.91,
                "frameIndex" to 42,
                "bbox" to listOf(100, 80, 240, 160)
            )
        )

        return InferResponse(
            ok = true,
            defects = defects
        )
    }
}