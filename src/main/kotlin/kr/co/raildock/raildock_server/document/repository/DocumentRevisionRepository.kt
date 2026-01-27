package kr.co.raildock.raildock_server.document.repository

import kr.co.raildock.raildock_server.document.entity.DocumentRevision
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DocumentRevisionRepository : JpaRepository<DocumentRevision, UUID> {

    fun findByDocumentIdOrderByRevisionVersionDesc(documentId: UUID): List<DocumentRevision>

    fun findTopByDocumentIdOrderByRevisionVersionDesc(documentId: UUID): DocumentRevision?

    fun deleteByDocumentId(documentId: UUID)

    fun findByIdAndDocumentId(
        id: UUID,
        documentId: UUID
    ): DocumentRevision?
}
