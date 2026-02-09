package kr.co.raildock.raildock_server.integration.vision

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class VisionClientImpl(
    @Value("\${integration.vision.url}") private val fastApiUrl: String
) : VisionClient {

    private val log = LoggerFactory.getLogger(javaClass)

    private val client: WebClient = WebClient.builder()
        .baseUrl(fastApiUrl)
        .codecs { codecs ->
            codecs.defaultCodecs().maxInMemorySize(50 * 1024 * 1024) // 50MB
        }
        .build()

    override fun infer(req: VisionInferRequest): ByteArray {
        log.info("FastAPI infer request={}", req)

        return client.post()
            .uri("/infer")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.valueOf("application/zip"))
            .bodyValue(req)
            .exchangeToMono { resp ->
                val ct = resp.headers().contentType().orElse(null)
                log.info("FastAPI response status={} contentType={}", resp.statusCode().value(), ct)

                if (resp.statusCode().is2xxSuccessful) {
                    resp.bodyToMono(ByteArray::class.java)
                } else {
                    resp.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .flatMap { body ->
                            val msg = buildString {
                                append("FastAPI /infer failed: status=")
                                append(resp.statusCode().value())
                                if (body.isNotBlank()) {
                                    append(", body=")
                                    append(body.take(2000))
                                }
                            }
                            Mono.error(IllegalStateException(msg))
                        }
                }
            }
            .timeout(Duration.ofMinutes(30))
            .block() ?: throw IllegalStateException("FastAPI returned empty body")
    }

    override fun inferHealthCheck(): VisionHealthResponse {
        log.info("FastAPI infer health check")

        return client.get()
            .uri("/health")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(VisionHealthResponse::class.java)
            .timeout(Duration.ofSeconds(10))
            .block() ?: throw IllegalStateException("FastAPI /health returned empty body")
    }

    override fun feedbackUrl(req: FeedbackUrlRequest): FeedbackUrlResponse {
        log.info("FastAPI feedback_url request: zipUrl={}, overwrite={}", req.zipUrl, req.overwrite)

        return client.post()
            .uri("/feedback_url")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(req)
            .exchangeToMono { resp ->
                if (resp.statusCode().is2xxSuccessful) {
                    resp.bodyToMono(FeedbackUrlResponse::class.java)
                } else {
                    resp.bodyToMono(String::class.java)
                        .defaultIfEmpty("")
                        .flatMap { body ->
                            val msg = "FastAPI /feedback_url failed: status=${resp.statusCode().value()}, body=${body.take(500)}"
                            Mono.error(IllegalStateException(msg))
                        }
                }
            }
            .timeout(Duration.ofMinutes(5))
            .block() ?: throw IllegalStateException("FastAPI /feedback_url returned empty body")
    }

    override fun getFinetuneStatus(jobId: String): FinetuneStatusResponse {
        log.info("FastAPI finetune status: jobId={}", jobId)

        return client.get()
            .uri("/finetune/{jobId}", jobId)
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono(FinetuneStatusResponse::class.java)
            .timeout(Duration.ofSeconds(30))
            .block() ?: throw IllegalStateException("FastAPI /finetune/$jobId returned empty body")
    }
}