package kr.co.raildock.raildock_server.integration.llm.chatbot

import jakarta.validation.constraints.NotBlank

data class ChatbotAskRequest(
    @field:NotBlank
    val question: String,

    // 선택 옵션 (문서에 언급된 folder_filter 대응)
    val folderFilter: String? = null
)

data class ChatbotAskResponse(
    val answer: String,
    val relatedReports: List<RelatedReport> = emptyList(),
    val reportCount: Int = 0
)

data class RelatedReport(
    val reportId: String,
    val folder: String,
    val filename: String,
    val riskGrade: String,
    val defectTypes: String
)