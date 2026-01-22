package kr.co.raildock.raildock_server.detect.dto

data class InferRequest(
    val videoId: Long,
    val videoType: String,
    val videoUrl: String,
)

data class InferResponse(
    val ok: Boolean,
    val defects: List<Any> = emptyList() // 다음 단계에서 Defect DTO로 구현
)