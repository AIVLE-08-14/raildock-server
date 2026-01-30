package kr.co.raildock.raildock_server.integration.vision

data class VisionInferRequest(
    val rail_mp4: String? = null,
    val insulator_mp4: String? = null,
    val nest_mp4: String? = null,
    val conf: Double? = 0.25,
    val iou: Double? = 0.7,
    val stride: Int? = 5
)

data class VisionHealthResponse(
    val ok: Boolean,
)