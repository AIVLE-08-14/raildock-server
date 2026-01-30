package kr.co.raildock.raildock_server.integration.llm

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class LlmWebConfig(
    @Value("\${integration.llm.url}") private val llmUrl: String,
) {
    @Bean
    fun llmWebClient(): WebClient{
        return WebClient.builder()
            .baseUrl(llmUrl)
            .codecs{ codecs ->
                codecs.defaultCodecs().maxInMemorySize(50 * 1024 * 1024) // 50MB
            }
            .build()
    }
}