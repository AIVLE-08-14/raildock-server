package kr.co.raildock.raildock_server.file.service

import kr.co.raildock.raildock_server.file.dto.UploadFileResponse
import kr.co.raildock.raildock_server.file.entity.FileType
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile

interface FileService {
    fun upload(
        file: MultipartFile,
        fileType: FileType
    ): UploadFileResponse

    fun download(fileId: Long): ResponseEntity<Resource>

    fun deleteFile(fileId: Long)
}
