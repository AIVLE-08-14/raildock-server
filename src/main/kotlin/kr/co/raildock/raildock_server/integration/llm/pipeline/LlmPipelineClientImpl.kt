package kr.co.raildock.raildock_server.integration.llm.pipeline

import kr.co.raildock.raildock_server.integration.llm.dto.ProcessVideoRequest
import kr.co.raildock.raildock_server.integration.llm.dto.ProcessVideoResponse
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class LlmPipelineClientImpl(
    private val llmWebClient: WebClient,
): LlmPipelineClient {
    override fun processVideo(
        videoUrl: String,
        originalMetadataUrl: String,
        generatePdf: Boolean,
        skipReview: Boolean
    ): ProcessVideoResponse {
        val request = ProcessVideoRequest(
            videoUrl = videoUrl,
            originalMetadataUrl = originalMetadataUrl,
            generatePdf = generatePdf,
            skipReview = skipReview
        )
        return llmWebClient.post()
            .uri("/pipeline/process-zip")
            .bodyValue(request)
            .retrieve()
            .onStatus({ it.isError }) { resp ->
                resp.bodyToMono<String>()
                    .defaultIfEmpty("")
                    .map { raw ->
                        IllegalStateException(
                            "LLM process-zip failed: status=${resp.statusCode().value()}, body=${raw.take(2000)}"
                        )
                    }
            }
                .bodyToMono<ProcessVideoResponse>()
            .timeout(Duration.ofMinutes(30))
            .block()
            ?: throw IllegalStateException("LLM returned empty body")
    }
}
