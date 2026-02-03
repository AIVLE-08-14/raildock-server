package kr.co.raildock.raildock_server.integration.llm.pipeline

import kr.co.raildock.raildock_server.integration.llm.dto.ProcessVideoRequest
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
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
    ): ByteArray {
        val request = ProcessVideoRequest(
            videoUrl = videoUrl,
            originalMetadataUrl = originalMetadataUrl,
            generatePdf = generatePdf,
            skipReview = skipReview
        )

        val zipMediaType = MediaType("application", "zip")

        return llmWebClient.post()
            .uri("/pipeline/process-zip")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(zipMediaType)
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
                .bodyToMono<ByteArray>()
            .timeout(Duration.ofMinutes(30))
            .block()
            ?: throw IllegalStateException("LLM returned empty body")
    }
}
