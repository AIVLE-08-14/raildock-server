package kr.co.raildock.raildock_server.file.service

import kr.co.raildock.raildock_server.config.AwsProperties
import kr.co.raildock.raildock_server.file.dto.UploadFileResponse
import kr.co.raildock.raildock_server.file.entity.FileEntity
import kr.co.raildock.raildock_server.file.enum.FileType
import kr.co.raildock.raildock_server.file.repository.FileRepository
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.MediaType.parseMediaType
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.time.LocalDate
import java.util.UUID.randomUUID
import java.util.zip.ZipInputStream

@Service
class FileServiceImpl(
    private val fileRepository: FileRepository,
    private val s3Client: S3Client,
    private val awsProperties: AwsProperties
) : FileService {

    override fun upload(file: MultipartFile, fileType: FileType): UploadFileResponse {
        val originalFilename = file.originalFilename ?: "unknown"
        val contentType = file.contentType ?: "application/octet-stream"
        val size = file.size

        file.inputStream.use {input ->
            return uploadInternal(
                requestBody = RequestBody.fromInputStream(input, size),
                size = size,
                originalFilename = originalFilename,
                contentType = contentType,
                fileType = fileType
            )
        }
    }

    override fun uploadBytes(
        bytes: ByteArray,
        originalFilename: String,
        contentType: String,
        fileType: FileType
    ): UploadFileResponse {
        return uploadInternal(
            requestBody = RequestBody.fromBytes(bytes),
            size = bytes.size.toLong(),
            originalFilename = originalFilename,
            contentType = contentType,
            fileType = fileType
        )
    }

    override fun unzipAndUpload(zipBytes: ByteArray, parentId: Long?): List<UploadFileResponse> {
        val results = mutableListOf<UploadFileResponse>()

        ByteArrayInputStream(zipBytes).use { bais ->
            ZipInputStream(bais).use { zis ->
                while (true) {
                    val entry = zis.nextEntry ?: break
                    if (entry.isDirectory) continue

                    val filename = entry.name.substringAfterLast('/')
                    val ext = filename.substringAfterLast('.', "").lowercase()

                    val contentType = when (ext) {
                        "pdf" -> "application/pdf"
                        "json" -> "application/json"
                        else -> {
                            zis.closeEntry()
                            continue
                        }
                    }

                    val bytes = zis.readBytes()

                    val fileType = when (ext) {
                        "pdf" -> FileType.PDF
                        "json" -> FileType.JSON
                        else -> FileType.ZIP
                    }

                    val finalName =
                        if (parentId != null) "pd-$parentId-$filename" else filename

                    results += uploadBytes(
                        bytes = bytes,
                        originalFilename = finalName,
                        contentType = contentType,
                        fileType = fileType
                    )

                    zis.closeEntry()
                }
            }
        }
        return results
    }

    private fun uploadInternal(
        requestBody: RequestBody,
        size: Long,
        originalFilename: String,
        contentType: String,
        fileType: FileType
    ): UploadFileResponse {
        val s3Key = generateS3Key(fileType, originalFilename)

        val putReq = PutObjectRequest.builder()
            .bucket(awsProperties.s3.bucket)
            .key(s3Key)
            .contentType(contentType)
            .build()
        s3Client.putObject(putReq, requestBody)

        val saved = fileRepository.save(
            FileEntity(
                s3Key = s3Key,
                fileType = fileType,
                contentType = contentType,
                size = size,
                originalFilename = originalFilename,
                bucket = awsProperties.s3.bucket
            )
        )

        return UploadFileResponse(
            fileId = saved.id,
            s3Key = saved.s3Key,
            bucket = saved.bucket,
            originalFilename = saved.originalFilename,
            contentType = saved.contentType,
            size = saved.size
        )
    }

    /**
     * 🔹 CloudFront URL 반환
     */
    override fun getdownloadURL(fileId: Long): ResponseEntity<String> {
        val file = fileRepository.findByIdAndStatus(fileId)
            ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")

        val url = "${awsProperties.cloudfront.domain}/${file.s3Key}"

        return ResponseEntity.ok(url)
    }

    // !!!이게 새로 만든거!!!
    override fun getDownloadUrl(fileId: Long): String{
        val file = fileRepository.findByIdAndStatus(fileId)
            ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")
        return "${awsProperties.cloudfront.domain}/${file.s3Key}"
    }

    /**
     * ⚠️ 내부 서버 다운로드 (점진적 제거 대상)
     */
    override fun download(fileId: Long): ResponseEntity<Resource> {
        val file = fileRepository.findByIdAndStatus(fileId)
            ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")

        val getReq = GetObjectRequest.builder()
            .bucket(file.bucket)
            .key(file.s3Key)
            .build()

        val s3Object = s3Client.getObject(getReq)
        val resource = InputStreamResource(s3Object)

        return ResponseEntity.ok()
            .header(CONTENT_DISPOSITION, "attachment; filename=\"${file.originalFilename}\"")
            .contentType(parseMediaType(file.contentType))
            .contentLength(file.size)
            .body(resource)
    }

    override fun deleteFile(fileId: Long) {
        val file = fileRepository.findByIdAndStatus(fileId)
            ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")

        file.markDeleted()
        fileRepository.save(file)
    }

    private fun generateS3Key(fileType: FileType, originalFilename: String): String {
        val ext = originalFilename.substringAfterLast('.', "")
        val date = LocalDate.now()
        val uuid = randomUUID()
        return "${fileType.name.lowercase()}/$date/$uuid.$ext"
    }

    override fun openStream(fileId: Long): InputStream {
        val file = fileRepository.findByIdAndStatus(fileId)
            ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")

        val getReq = GetObjectRequest.builder()
            .bucket(file.bucket)
            .key(file.s3Key)
            .build()

        // 🔥 ResponseInputStream<GetObjectResponse>
        return s3Client.getObject(getReq)
    }
}
