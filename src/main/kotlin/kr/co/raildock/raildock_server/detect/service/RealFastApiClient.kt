package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.dto.InferRequest
import kr.co.raildock.raildock_server.detect.dto.InferResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import java.time.Duration

@Profile("!mock") // mock 프로필이 아닐 때만 활성화
@Component
class RealFastApiClient(
    @Value("\${fastapi.base-url}") private val baseUrl: String,
) : FastApiClient {

    private val client = WebClient.builder()
        .baseUrl(baseUrl)
        .build()

    override fun infer(req: InferRequest): InferResponse {
        return client.post()
            .uri("/infer")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .retrieve()
            .bodyToMono(InferResponse::class.java)
            .timeout(Duration.ofMinutes(10))
            .block() ?: throw IllegalStateException("FastAPI returned empty response")
    }
}