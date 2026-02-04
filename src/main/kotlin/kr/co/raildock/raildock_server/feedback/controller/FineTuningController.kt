package kr.co.raildock.raildock_server.feedback.controller

import jakarta.servlet.http.HttpServletResponse
import kr.co.raildock.raildock_server.feedback.dto.FineTuningJobResponse
import kr.co.raildock.raildock_server.feedback.service.FineTuningService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/fine-tuning")
class FineTuningController(
    private val fineTuningService: FineTuningService
) {

    /** 테스트용 ZIP 다운로드 */
    @GetMapping("/download")
    fun downloadZip(response: HttpServletResponse) {
        response.contentType = "application/zip"
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=fine_tuning.zip"
        )

        fineTuningService.writeZipTo(response.outputStream)
    }

    // TODO : 후성씨 여기부분 부탁해요
    /** 파인튜닝 시작 */
    @PostMapping("/start")
    fun start(): FineTuningJobResponse =
        fineTuningService.startFineTuning()
}

