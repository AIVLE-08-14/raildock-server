package kr.co.raildock.raildock_server.user.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "health",
    description = "서버 상태 체크용 controller"
)
@RestController
class HealthController {
    @GetMapping("/health")
    fun health(): Map<String, Any> =
        mapOf("status" to "UP")
}