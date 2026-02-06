package kr.co.raildock.raildock_server.file.service

import kr.co.raildock.raildock_server.file.dto.UploadFileResponse
import kr.co.raildock.raildock_server.file.enum.FileType
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.web.multipart.MultipartFile
import java.io.InputStream

interface FileService {

    // 파일 업로드
    fun upload(
        file: MultipartFile,
        fileType: FileType
    ): UploadFileResponse

    // 바이트 배열 업로드
    fun uploadBytes(
        bytes: ByteArray,
        originalFilename: String,
        contentType: String,
        fileType: FileType,
        parentId: Long? = null,
    ): UploadFileResponse

    // 파일 다운로드
    fun download(fileId: Long): ResponseEntity<Resource>

    // 바이트 배열 다운로드
    fun downloadBytes(url: String): ByteArray

    fun getdownloadURL(fileId: Long): ResponseEntity<String>
    // !!! 이게 새로 만든거 !!!
    fun getDownloadUrl(fileId: Long): String

    fun findFileId(parentId: Long, originalFilename: String): Long?

    fun deleteFile(fileId: Long)

    fun openStream(fileId: Long): InputStream
}
