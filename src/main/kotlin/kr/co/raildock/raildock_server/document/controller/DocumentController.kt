package kr.co.raildock.raildock_server.document.controller

import kr.co.raildock.raildock_server.document.dto.*
import kr.co.raildock.raildock_server.document.service.DocumentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@RestController
@RequestMapping("/documents")
class DocumentController(
    private val documentService: DocumentService
) {

    @GetMapping("/list")
    fun getDocuments(): ResponseEntity<List<DocumentSummaryDto>> =
        ResponseEntity.ok(documentService.getDocuments())

    /* =========================
       문서 상세
    ========================= */
    @GetMapping("/{documentId}")
    fun getDocumentDetail(
        @PathVariable documentId: UUID
    ): ResponseEntity<DocumentDetailDto> =
        ResponseEntity.ok(documentService.getDocumentDetail(documentId))

    /* =========================
       문서 생성
    ========================= */
    @PostMapping
    fun createDocument(
        @RequestBody request: DocumentCreateRequest
    ): ResponseEntity<UUID> =
        ResponseEntity.ok(documentService.createDocument(request))

    /* =========================
        문서 수정
    ========================= */
    @PatchMapping("/{documentId}")
    fun updateDocument(
        @PathVariable documentId: UUID,
        @RequestBody request: DocumentUpdateRequest
    ): ResponseEntity<Void> {
        documentService.updateDocument(documentId, request)
        return ResponseEntity.ok().build()
    }

    /* =========================
        문서 삭제
    ========================= */
    @DeleteMapping("/{documentId}")
    fun deleteDocument(
        @PathVariable documentId: UUID
    ): ResponseEntity<Void> {
        documentService.deleteDocument(documentId)
        return ResponseEntity.noContent().build()
    }



    /* =========================
       문서 개정 업로드 (PDF + documentId)
    ========================= */
    @PostMapping(
        path = ["/{documentId}/revisions"],
        consumes = ["multipart/form-data"]
    )
    fun addRevision(
        @PathVariable documentId: UUID,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<Void> {
        documentService.addRevision(documentId, file)
        return ResponseEntity.ok().build()
    }

    /* =========================
    개정 이력 수정 (changeLog)
    ========================= */
    @PutMapping("/{documentId}/revisions/{revisionId}")
    fun updateRevision(
        @PathVariable documentId: UUID,
        @PathVariable revisionId: UUID,
        @RequestBody request: DocumentRevisionUpdateRequest
    ): ResponseEntity<Void> {
        documentService.updateRevision(documentId, revisionId, request)
        return ResponseEntity.ok().build()
    }

    /* =========================
    개정 이력 삭제 (최신만)
    ========================= */
    @DeleteMapping("/{documentId}/revisions/{revisionId}")
    fun deleteRevision(
        @PathVariable documentId: UUID,
        @PathVariable revisionId: UUID
    ): ResponseEntity<Void> {
        documentService.deleteRevision(documentId, revisionId)
        return ResponseEntity.noContent().build()
    }

}
