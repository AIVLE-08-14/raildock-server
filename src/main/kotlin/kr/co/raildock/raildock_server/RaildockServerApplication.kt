package kr.co.raildock.raildock_server

import kr.co.raildock.raildock_server.config.S3Properties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties(S3Properties::class)
class RaildockServerApplication

fun main(args: Array<String>) {
	runApplication<RaildockServerApplication>(*args)
}
