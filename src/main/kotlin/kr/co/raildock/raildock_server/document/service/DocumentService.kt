package kr.co.raildock.raildock_server.document.service

import kr.co.raildock.raildock_server.document.dto.*
import kr.co.raildock.raildock_server.document.entity.Document
import kr.co.raildock.raildock_server.document.entity.DocumentRevision
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
       문서 상세 + 연혁
    ========================= */
    @Transactional(readOnly = true)
    fun getDocumentDetail(documentId: UUID): DocumentDetailDto {
        val document = documentRepository.findById(documentId)
            .orElseThrow { IllegalArgumentException("Document not found") }

        val revisions = revisionRepository
            .findByDocumentIdOrderByRevisionVersionDesc(documentId)
            .map {
                DocumentRevisionDto(
                    revisionId = it.id!!,
                    version = it.revisionVersion,
                    changeLog = it.changeLog,
                    createdAt = it.createdAt,
                    createdBy = it.createdBy,
                    downloadUrl = fileService.getdownloadURL(it.fileId).toString()
                )
            }

        return DocumentDetailDto(
            id = document.id!!,
            name = document.name,
            description = document.description,
            createdAt = document.createdAt,
            revisions = revisions
        )
    }

    /* =========================
       문서 생성
    ========================= */
    @Transactional
    fun createDocument(request: DocumentCreateRequest): UUID {
        val document = documentRepository.save(
            Document(
                name = request.name,
                description = request.description
            )
        )
        return document.id!!
    }

    @Transactional
    fun addRevision(
        documentId: UUID,
        file: MultipartFile
    ) {
        // 1️⃣ 문서 존재 확인
        val document = documentRepository.findById(documentId)
            .orElseThrow { IllegalArgumentException("Document not found") }

        // 2️⃣ 파일 타입 검증 (PDF만 허용)
        validatePdf(file)

        // 3️⃣ 파일 업로드 → fileId 획득
        val uploadResponse = fileService.upload(
            file = file,
            fileType = FileType.PDF
        )
        val fileId = uploadResponse.fileId

        // 4️⃣ 작성자 (로그인 사용자)
        val createdBy = userService.getMyId()

        // 5️⃣ 다음 revisionVersion 계산
        val nextVersion =
            (revisionRepository.findTopByDocumentIdOrderByRevisionVersionDesc(documentId)
                ?.revisionVersion ?: 0) + 1

        // 6️⃣ 개정 이력 저장
        revisionRepository.save(
            DocumentRevision(
                document = document,
                revisionVersion = nextVersion,
                createdBy = createdBy,
                fileId = fileId
            )
        )
    }

    private fun validatePdf(file: MultipartFile) {
        val contentType = file.contentType ?: ""

        if (contentType != "application/pdf") {
            throw IllegalArgumentException("Only PDF files are allowed")
        }
    }

    @Transactional
    fun updateDocument(
        documentId: UUID,
        request: DocumentUpdateRequest
    ) {
        val document = documentRepository.findById(documentId)
            .orElseThrow { IllegalArgumentException("Document not found") }

        request.name?.let {
            // 이름 중복 체크
            if (it != document.name && documentRepository.existsByName(it)) {
                throw IllegalArgumentException("Duplicate document name")
            }
            document.name = it
        }

        request.description?.let {
            document.description = it
        }
    }

    @Transactional
    fun deleteDocument(documentId: UUID) {
        val document = documentRepository.findById(documentId)
            .orElseThrow { IllegalArgumentException("Document not found") }

        // revision 먼저 삭제
        revisionRepository.deleteByDocumentId(documentId)

        documentRepository.delete(document)
    }

    @Transactional
    fun updateRevision(
        documentId: UUID,
        revisionId: UUID,
        request: DocumentRevisionUpdateRequest
    ) {
        val revision = revisionRepository
            .findByIdAndDocumentId(revisionId, documentId)
            ?: throw IllegalArgumentException("Revision not found")

        // changeLog만 수정 허용
        revision.changeLog = request.changeLog
    }

    @Transactional
    fun deleteRevision(
        documentId: UUID,
        revisionId: UUID
    ) {
        val latestRevision =
            revisionRepository.findTopByDocumentIdOrderByRevisionVersionDesc(documentId)
                ?: throw IllegalArgumentException("No revisions found")

        if (latestRevision.id != revisionId) {
            throw IllegalStateException("Only latest revision can be deleted")
        }

        revisionRepository.delete(latestRevision)

        // 파일까지 같이 삭제
         fileService.deleteFile(latestRevision.fileId)
    }


}
