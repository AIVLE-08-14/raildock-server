package kr.co.raildock.raildock_server

import kr.co.raildock.raildock_server.config.AwsProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
@EnableConfigurationProperties(AwsProperties::class)
class RaildockServerApplication

fun main(args: Array<String>) {
	runApplication<RaildockServerApplication>(*args)
}
