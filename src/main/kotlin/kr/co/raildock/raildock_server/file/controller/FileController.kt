package kr.co.raildock.raildock_server.file.controller

import io.swagger.v3.oas.annotations.tags.Tag
import kr.co.raildock.raildock_server.file.dto.UploadFileResponse
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
// TODO : cloud front 방식으로 업그레이드하기
@Tag(
    name = "Files",
    description = "파일관리 테스트용 controller"
)
@RestController
@RequestMapping("/api/files")
class FileController(
    private val fileService: FileService
) {

    @PostMapping(consumes = ["multipart/form-data"])
    fun upload(
        @RequestPart("file") file: MultipartFile,
        @RequestParam("fileType") fileType: FileType
    ): ResponseEntity<UploadFileResponse> {
        return ResponseEntity.ok(
            fileService.upload(file, fileType)
        )
    }

    @GetMapping("/{fileId}/download")
    fun download(
        @PathVariable fileId: Long
    ): ResponseEntity<Resource> {
        return fileService.download(fileId)
    }

    @GetMapping("/{fileId}/download-url")
    fun getDownloadUrl(
        @PathVariable fileId: Long
    ): ResponseEntity<String> {
        return fileService.getdownloadURL(fileId)
    }

    @DeleteMapping("/{fileId}")
    fun deleteFile(
        @PathVariable fileId: Long
    ): ResponseEntity<Void> {
        fileService.deleteFile(fileId)
        return ResponseEntity.noContent().build()
    }
}