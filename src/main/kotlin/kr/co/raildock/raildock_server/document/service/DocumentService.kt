package kr.co.raildock.raildock_server.document.service

import kr.co.raildock.raildock_server.common.exception.BusinessException
import kr.co.raildock.raildock_server.document.dto.*
import kr.co.raildock.raildock_server.document.entity.Document
import kr.co.raildock.raildock_server.document.entity.DocumentRevision
import kr.co.raildock.raildock_server.document.exception.DocumentErrorCode
import kr.co.raildock.raildock_server.document.repository.DocumentRepository
import kr.co.raildock.raildock_server.document.repository.DocumentRevisionRepository
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import kr.co.raildock.raildock_server.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Service
class DocumentService(
    private val documentRepository: DocumentRepository,
    private val revisionRepository: DocumentRevisionRepository,
    private val fileService: FileService,
    private val userService: UserService
) {

    /* =========================
       문서 목록
    ========================= */
    @Transactional(readOnly = true)
    fun getDocuments(): List<DocumentSummaryDto> {
        return documentRepository.findAll().map { doc ->
            val latestRevision =
                revisionRepository.findTopByDocumentIdOrderByRevisionVersionDesc(doc.id!!)

            DocumentSummaryDto(
                id = doc.id,
                name = doc.name,
                latestVersion = latestRevision?.revisionVersion ?: 0,
                updatedAt = latestRevision?.createdAt ?: doc.createdAt
            )
        }
    }

    /* =========================
       문서 상세 + 최신 + 이력
    ========================= */
    @Transactional(readOnly = true)
    fun getDocumentDetail(documentId: UUID): DocumentDetailDto {
        val document = documentRepository.findById(documentId)
            .orElseThrow { BusinessException(DocumentErrorCode.DOCUMENT_NOT_FOUND) }

        val revisions =
            revisionRepository.findByDocumentIdOrderByRevisionVersionDesc(documentId)

        if (revisions.isEmpty()) {
            throw BusinessException(DocumentErrorCode.DOCUMENT_HAS_NO_REVISION)
        }

        val latest = revisions.first()
        val history = revisions.drop(1)

        fun toDto(it: DocumentRevision) = DocumentRevisionDto(
            revisionId = it.id!!,
            version = it.revisionVersion,
            changeLog = it.changeLog,
            createdAt = it.createdAt,
            createdBy = it.createdBy,
            downloadUrl = fileService.getdownloadURL(it.fileId).body!!
        )

        return DocumentDetailDto(
            id = document.id!!,
            name = document.name,
            description = document.description,
            createdAt = document.createdAt,
            latestRevision = toDto(latest),
            history = history.map { toDto(it) }
        )
    }

    /* =========================
       문서 생성
    ========================= */
    @Transactional
    fun createDocument(request: DocumentCreateRequest): UUID {
        if (documentRepository.existsByName(request.name)) {
            throw BusinessException(DocumentErrorCode.DUPLICATE_DOCUMENT_NAME)
        }

        val document = documentRepository.save(
            Document(
                name = request.name,
                description = request.description
            )
        )
        return document.id!!
    }

    /* =========================
       문서 수정
    ========================= */
    @Transactional
    fun updateDocument(
        documentId: UUID,
        request: DocumentUpdateRequest
    ) {
        val document = documentRepository.findById(documentId)
            .orElseThrow { BusinessException(DocumentErrorCode.DOCUMENT_NOT_FOUND) }

        request.name?.let {
            if (it != document.name && documentRepository.existsByName(it)) {
                throw BusinessException(DocumentErrorCode.DUPLICATE_DOCUMENT_NAME)
            }
            document.name = it
        }

        request.description?.let {
            document.description = it
        }
    }

    /* =========================
       문서 삭제
    ========================= */
    @Transactional
    fun deleteDocument(documentId: UUID) {
        val document = documentRepository.findById(documentId)
            .orElseThrow { BusinessException(DocumentErrorCode.DOCUMENT_NOT_FOUND) }

        revisionRepository.deleteByDocumentId(documentId)
        documentRepository.delete(document)
    }

    /* =========================
       개정 추가 (PDF)
    ========================= */
    @Transactional
    fun addRevision(
        documentId: UUID,
        file: MultipartFile
    ) {
        val document = documentRepository.findById(documentId)
            .orElseThrow { BusinessException(DocumentErrorCode.DOCUMENT_NOT_FOUND) }

        if (file.contentType != "application/pdf") {
            throw BusinessException(DocumentErrorCode.INVALID_FILE_TYPE)
        }

        val uploadResponse = fileService.upload(
            file = file,
            fileType = FileType.PDF
        )

        val createdBy = userService.getMyId()

        val nextVersion =
            (revisionRepository.findTopByDocumentIdOrderByRevisionVersionDesc(documentId)
                ?.revisionVersion ?: 0) + 1

        revisionRepository.save(
            DocumentRevision(
                document = document,
                revisionVersion = nextVersion,
                createdBy = createdBy,
                fileId = uploadResponse.fileId
            )
        )
    }

    /* =========================
       개정 로그 수정
    ========================= */
    @Transactional
    fun updateRevision(
        documentId: UUID,
        revisionId: UUID,
        request: DocumentRevisionUpdateRequest
    ) {
        val revision = revisionRepository
            .findByIdAndDocumentId(revisionId, documentId)
            ?: throw BusinessException(DocumentErrorCode.REVISION_NOT_FOUND)

        revision.changeLog = request.changeLog
    }

    /* =========================
       개정 삭제 (최신만)
    ========================= */
    @Transactional
    fun deleteRevision(
        documentId: UUID,
        revisionId: UUID
    ) {
        val latestRevision =
            revisionRepository.findTopByDocumentIdOrderByRevisionVersionDesc(documentId)
                ?: throw BusinessException(DocumentErrorCode.DOCUMENT_HAS_NO_REVISION)

        if (latestRevision.id != revisionId) {
            throw BusinessException(DocumentErrorCode.ONLY_LATEST_REVISION_CAN_BE_DELETED)
        }

        revisionRepository.delete(latestRevision)
        fileService.deleteFile(latestRevision.fileId)
    }
}
