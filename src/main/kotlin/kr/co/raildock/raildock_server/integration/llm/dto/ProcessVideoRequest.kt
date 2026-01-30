package kr.co.raildock.raildock_server.integration.llm.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class ProcessVideoRequest(
    @JsonProperty("video_url")
    val videoUrl: String,

    @JsonProperty("original_metadata_url")
    val originalMetadataUrl: String? = null,

    @JsonProperty("generate_pdf")
    val generatePdf: Boolean = true,

    @JsonProperty("skip_review")
    val skipReview: Boolean = false
)
