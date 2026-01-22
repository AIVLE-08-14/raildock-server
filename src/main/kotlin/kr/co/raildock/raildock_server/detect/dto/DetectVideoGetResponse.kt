package kr.co.raildock.raildock_server.detect.dto

import java.time.OffsetDateTime

data class DetectVideoGetResponse(
    val videoId: Long,
    val videoType: String,
    val status: String,
    val errorMessage: String?
)
