package kr.co.raildock.raildock_server.document.service

import kr.co.raildock.raildock_server.document.dto.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

interface DocumentService {

    fun getDocuments(): List<DocumentSummaryDto>

    fun getDocumentDetail(documentId: UUID): DocumentDetailDto

    fun createDocument(request: DocumentCreateRequest): UUID

    fun updateDocument(
        documentId: UUID,
        request: DocumentUpdateRequest
    )

    fun deleteDocument(documentId: UUID)

    fun addRevision(
        documentId: UUID,
        file: MultipartFile
    )

    fun deleteRevision(
        documentId: UUID,
        revisionId: UUID
    )
}