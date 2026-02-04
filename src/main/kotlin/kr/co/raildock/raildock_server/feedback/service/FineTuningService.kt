package kr.co.raildock.raildock_server.feedback.service

import kr.co.raildock.raildock_server.feedback.dto.FineTuningJobResponse
import java.io.OutputStream

interface FineTuningService {

    /** 데이터 다운로드 테스트용 */
    fun writeZipTo(outputStream: OutputStream)

    /** 실제 파인튜닝 실행용 */
    fun startFineTuning(): FineTuningJobResponse
}
