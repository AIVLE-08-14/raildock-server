package kr.co.raildock.raildock_server.integration.llm.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProcessVideoResponse(
    val message: String? = null,

    @JsonProperty("total_processed")
    val totalProcessed: Int? = null,

    val summary: Map<String, Any>? = null,

    val results: List<ProcessVideoResultItem> = emptyList(),

    @JsonProperty("pdf_paths")
    val pdfPaths: Map<String, String>? = null

)

data class ProcessVideoResultItem(
    val filename: String? = null,
    val folder: String? = null,
    val document: String? = null,

    @JsonProperty("pdf_path")
    val pdfPath: String? = null
)