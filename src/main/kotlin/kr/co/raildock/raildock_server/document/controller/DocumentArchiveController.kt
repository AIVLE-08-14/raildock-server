package kr.co.raildock.raildock_server.document.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletResponse
import kr.co.raildock.raildock_server.document.service.DocumentArchiveService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(
    name = "DocumentArchive",
    description = "유지보수 문서 및 개정이력 전체 다운로드 - 나중에 호출제한 걸어야함"
)
@RestController
@RequestMapping("/archive")
class DocumentArchiveController(
    private val documentArchiveService: DocumentArchiveService
) {

    @GetMapping("/documents")
    @Operation(summary = "전체 유지보수 문서 및 개정이력 다운로드 - 필요시 연락")
    fun downloadAllDocuments(response: HttpServletResponse) {
        documentArchiveService.downloadAllDocumentsAsZip(response)
    }
}
