package kr.co.raildock.raildock_server.file.service

import kr.co.raildock.raildock_server.file.dto.GenerateUploadUrlRequest
import kr.co.raildock.raildock_server.file.dto.GenerateUploadUrlResponse

interface FileService {
    fun upload(
        file: org.springframework.web.multipart.MultipartFile,
        fileType: kr.co.raildock.raildock_server.file.entity.FileType
    ): kr.co.raildock.raildock_server.file.dto.UploadFileResponse

    fun download(fileId: Long): org.springframework.http.ResponseEntity<org.springframework.core.io.Resource>

    fun deleteFile(fileId: Long)
}
