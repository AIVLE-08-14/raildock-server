package kr.co.raildock.raildock_server.document.dto

import java.time.LocalDateTime
import java.util.UUID

data class DocumentSummaryDto(
    val id: UUID,
    val name: String,
    val latestVersion: Int,
    val createdAt: LocalDateTime
)

data class DocumentDetailDto(
    val id: UUID,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val history: List<DocumentRevisionDto>
)

data class DocumentRevisionDto(
    val revisionId: UUID,
    val version: Int,
    val createdAt: LocalDateTime,
    val createdBy: Long,
    val downloadUrl: String
)

data class DocumentCreateRequest(
    val name: String,
    val description: String
)


data class DocumentUpdateRequest(
    val name: String?,
    val description: String?
)