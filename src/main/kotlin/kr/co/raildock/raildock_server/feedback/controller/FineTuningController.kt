package kr.co.raildock.raildock_server.feedback.controller

import kr.co.raildock.raildock_server.feedback.dto.EngineerFineTuningPayload
import kr.co.raildock.raildock_server.feedback.dto.FineTuningJobResponse
import kr.co.raildock.raildock_server.feedback.service.FineTuningService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fine-tuning")
class FineTuningController(
    private val fineTuningService: FineTuningService
) {

    @PostMapping("/data")
    fun buildData(): EngineerFineTuningPayload =
        fineTuningService.buildFineTuningData()

    @PostMapping("/start")
    fun start(): FineTuningJobResponse =
        fineTuningService.startFineTuning()
}
