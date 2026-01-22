package kr.co.raildock.raildock_server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class RaildockServerApplication

fun main(args: Array<String>) {
	runApplication<RaildockServerApplication>(*args)
}
