package kr.co.raildock.raildock_server.detect.service

import kr.co.raildock.raildock_server.detect.dto.FastAPIInferRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class FastApiClientImpl(
    @Value("\${fastapi.url}") private val fastApiUrl: String
) : FastApiClient {

    private val log = LoggerFactory.getLogger(javaClass)

    private val client: WebClient = WebClient.builder()
        .baseUrl(fastApiUrl)
        .codecs { codecs ->
            codecs.defaultCodecs().maxInMemorySize(50 * 1024 * 1024) // 50MB
        }
        .build()

    override fun infer(req: FastAPIInferRequest): ByteArray {
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
}