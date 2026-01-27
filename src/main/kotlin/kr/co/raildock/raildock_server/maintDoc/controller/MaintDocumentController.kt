package kr.co.raildock.raildock_server.maintDoc.controller

import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.raildock.raildock_server.maintDoc.dto.MaintDocumentCreateRequestDTO
import kr.co.raildock.raildock_server.maintDoc.dto.MaintDocumentResponseDTO
import kr.co.raildock.raildock_server.maintDoc.dto.MaintDocumentUpdateRequestDTO
import kr.co.raildock.raildock_server.maintDoc.repository.MaintDocumentRepository
import kr.co.raildock.raildock_server.maintDoc.service.MaintDocumentService
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Document-previous",
    description = "유지보수 문서 관리"
)
@RestController
@RequestMapping("/maint-documents")
class MaintDocumentController(
    private val service: MaintDocumentService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody req: MaintDocumentCreateRequestDTO): MaintDocumentResponseDTO =
        service.create(req)

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): MaintDocumentResponseDTO =
        service.get(id)

    @GetMapping
    fun list(
        @RequestParam(name = "page", defaultValue = "0") page: Int,
        @RequestParam(name = "size", defaultValue = "10") size: Int
    ): Page<MaintDocumentResponseDTO> =
        service.list(page, size)

    @PatchMapping("/{id}")
    fun update(
        @PathVariable id: Long,
        @RequestBody req: MaintDocumentUpdateRequestDTO
    ): MaintDocumentResponseDTO = service.update(id, req)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(@PathVariable id: Long) =
        service.delete(id)
}