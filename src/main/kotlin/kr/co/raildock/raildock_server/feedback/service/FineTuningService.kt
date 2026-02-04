package kr.co.raildock.raildock_server.feedback.service

import kr.co.raildock.raildock_server.feedback.dto.EngineerFineTuningPayload
import kr.co.raildock.raildock_server.feedback.dto.FineTuningJobResponse

interface FineTuningService {
    fun buildFineTuningData(): EngineerFineTuningPayload
    fun startFineTuning(): FineTuningJobResponse
}

