package kr.co.raildock.raildock_server.detect.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

data class LlmReportRoot(
    val report_id: String,
    val dataset_type: String,
    val created_at: String,
    val metadata: UploadedMetadata? = null,
    val reports: List<LlmReportItem>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class UploadedMetadata(
    val width: Int? = null,
    val height: Int? = null,
    val fps: Int? = null,

    val datetime: String? = null,
    val region_name: String? = null,

    val weather: String? = null,
    val humidity: Int? = null,
    val temperature: Int? = null,

    val longitude: Double? = null,
    val latitude: Double? = null,
)

data class LlmReportItem(
    val index: Int,
    val image_file: String? = null,
    val vision_result: VisionResult,
    val document_sections: Map<String, String>? = null
)

data class VisionResult(
    val frame_index: Int,
    val timestamp_ms: Double,
    val detections: List<Detection>
)

data class Detection(
    val rail_type: String? = null,
    val cls_name: String? = null,
    val detail: String? = null,
    val confidence: Double? = null,
    val bounding_box: BoundingBox? = null
)

data class BoundingBox(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)