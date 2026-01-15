package kr.co.raildock.raildock_server.maintDoc.dto

import kr.co.raildock.raildock_server.maintDoc.entity.MaintDocument
import java.time.LocalDateTime

data class MaintDocumentCreateRequestDTO(
    val title: String,
    val content: String,
    val category: String? = null
)

data class MaintDocumentUpdateRequestDTO(
    val title: String? = null,
    val content: String? = null,
    val category: String? = null
)

data class MaintDocumentResponseDTO(
    val id: Long,
    val title: String,
    val content: String,
    val category: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
){
    companion object {
        fun from(entity: MaintDocument) = MaintDocumentResponseDTO(
            id = entity.id,
            title = entity.title,
            content = entity.content,
            category = entity.category,
            createdAt = entity.createdAt,
            updatedAt =  entity.updatedAt
        )
    }
}