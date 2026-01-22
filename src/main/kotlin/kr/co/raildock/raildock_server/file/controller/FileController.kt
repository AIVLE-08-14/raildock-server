package kr.co.raildock.raildock_server.file.controller

import kr.co.raildock.raildock_server.file.dto.UploadFileResponse
import kr.co.raildock.raildock_server.file.entity.FileType
import kr.co.raildock.raildock_server.file.service.FileService
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

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

    @DeleteMapping("/{fileId}")
    fun deleteFile(
        @PathVariable fileId: Long
    ): ResponseEntity<Void> {
        fileService.deleteFile(fileId)
        return ResponseEntity.noContent().build()
    }
}