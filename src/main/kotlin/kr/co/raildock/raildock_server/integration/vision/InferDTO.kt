package kr.co.raildock.raildock_server.integration.vision

import com.fasterxml.jackson.annotation.JsonProperty

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

// ===== Fine-tuning 관련 DTO =====

data class FeedbackUrlRequest(
    @JsonProperty("zip_url")
    val zipUrl: String,
    val overwrite: Boolean = false
)

data class FeedbackUrlResponse(
    val ok: Boolean,
    val summary: Map<String, TaskSummary>?,
    val overwrite: Boolean,
    @JsonProperty("finetune_job_id")
    val finetuneJobId: String?,
    @JsonProperty("finetune_config")
    val finetuneConfig: FinetuneConfig?,
    val source: String?
)

data class TaskSummary(
    val pairs: Int,
    val copied: Int,
    val skipped: Int
)

data class FinetuneConfig(
    val epochs: Int,
    val batch: Int,
    val imgsz: Int
)

data class FinetuneStatusResponse(
    @JsonProperty("job_id")
    val jobId: String,
    val status: Map<String, Any>
)