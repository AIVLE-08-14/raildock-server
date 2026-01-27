package kr.co.raildock.raildock_server.file.service

import kr.co.raildock.raildock_server.file.dto.UploadFileResponse
import kr.co.raildock.raildock_server.file.enum.FileType
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile

interface FileService {
    fun upload(
        file: MultipartFile,
        fileType: FileType
    ): UploadFileResponse

    fun uploadBytes(
        bytes: ByteArray,
        originalFilename: String,
        contentType: String,
        fileType: FileType
    ): UploadFileResponse

    fun download(fileId: Long): ResponseEntity<Resource>

    fun getdownloadURL(fileId: Long): ResponseEntity<String>

    // !!! 이게 새로 만든거 !!!
    fun getDownloadUrl(fileId: Long): String

    fun deleteFile(fileId: Long)
}
