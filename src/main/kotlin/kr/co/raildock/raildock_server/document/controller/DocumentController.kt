package kr.co.raildock.raildock_server.document.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.raildock.raildock_server.document.dto.*
import kr.co.raildock.raildock_server.document.service.DocumentService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.util.UUID

@Tag(
    name = "Document",
    description = "유지보수 문서 및 개정이력 관리"
)
@RestController
@RequestMapping("/documents")
class DocumentController(
    private val documentService: DocumentService
) {

    @GetMapping("/list")
    @Operation(summary = "유지보수 문서 리스트업")
    fun getDocuments(): ResponseEntity<List<DocumentSummaryDto>> =
        ResponseEntity.ok(documentService.getDocuments())

    @GetMapping("/{documentId}")
    @Operation(summary = "특정 유지보수 문서 개정이력 및 상세보기 (최신본 보여주기)")
    fun getDocumentDetail(
        @PathVariable documentId: UUID
    ): ResponseEntity<DocumentDetailDto> =
        ResponseEntity.ok(documentService.getDocumentDetail(documentId))

    @PostMapping
    @Operation(summary = "유지보수 파일 생성")
    fun createDocument(
        @RequestBody request: DocumentCreateRequest
    ): ResponseEntity<UUID> =
        ResponseEntity.ok(documentService.createDocument(request))

    @PatchMapping("/{documentId}")
    @Operation(summary = "유지보수 파일 수정")
    fun updateDocument(
        @PathVariable documentId: UUID,
        @RequestBody request: DocumentUpdateRequest
    ): ResponseEntity<Void> {
        documentService.updateDocument(documentId, request)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{documentId}")
    @Operation(summary = "유지보수 파일 삭제")
    fun deleteDocument(
        @PathVariable documentId: UUID
    ): ResponseEntity<Void> {
        documentService.deleteDocument(documentId)
        return ResponseEntity.noContent().build()
    }

    @PostMapping(
        path = ["/{documentId}/revisions"],
        consumes = ["multipart/form-data"]
    )
    @Operation(summary = "유지보수 개정 문서 추가 pdf 만 가능")
    fun addRevision(
        @PathVariable documentId: UUID,
        @RequestPart("file") file: MultipartFile
    ): ResponseEntity<Void> {
        documentService.addRevision(documentId, file)
        return ResponseEntity.ok().build()
    }

    @DeleteMapping("/{documentId}/revisions/{revisionId}")
    @Operation(summary = "개정 이력 삭제")
    fun deleteRevision(
        @PathVariable documentId: UUID,
        @PathVariable revisionId: UUID
    ): ResponseEntity<Void> {
        documentService.deleteRevision(documentId, revisionId)
        return ResponseEntity.noContent().build()
    }

}
