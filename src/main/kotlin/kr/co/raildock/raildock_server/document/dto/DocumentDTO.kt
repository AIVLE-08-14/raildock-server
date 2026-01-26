package kr.co.raildock.raildock_server.document.dto

import java.time.LocalDateTime
import java.util.UUID

/* =========================
   Document - 목록용 DTO
========================= */

data class DocumentSummaryDto(
    val id: UUID,
    val name: String,
    val latestVersion: Int,
    val updatedAt: LocalDateTime
)

/* =========================
   Document - 상세 DTO
========================= */

data class DocumentDetailDto(
    val id: UUID,
    val name: String,
    val description: String,
    val createdAt: LocalDateTime,
    val revisions: List<DocumentRevisionDto>
)

/* =========================
   Document Revision DTO
========================= */

data class DocumentRevisionDto(
    val revisionId: UUID,
    val version: Int,
    val changeLog: String?,
    val createdAt: LocalDateTime,
    val createdBy: Long,
    val downloadUrl: String
)

/* =========================
   Request DTOs
========================= */

/**
 * 문서 최초 생성
 */
data class DocumentCreateRequest(
    val name: String,
    val description: String
)

/**
 * 문서 개정 업로드
 * (파일은 MultipartFile로 별도 처리)
 */
data class DocumentRevisionCreateRequest(
    val changeLog: String?
)

data class DocumentUpdateRequest(
    val name: String?,
    val description: String?
)

data class DocumentRevisionUpdateRequest(
    val changeLog: String?
)
