package kr.co.raildock.raildock_server.file.service

import kr.co.raildock.raildock_server.file.dto.UploadFileResponse
import kr.co.raildock.raildock_server.file.enum.FileType
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

interface FileService {
    fun upload(
        file: MultipartFile,
        fileType: FileType
    ): UploadFileResponse

    fun download(fileId: Long): ResponseEntity<Resource>

    fun getdownloadURL(fileId: Long): ResponseEntity<String>

    fun deleteFile(fileId: Long)

    fun openStream(fileId: Long): InputStream
}
