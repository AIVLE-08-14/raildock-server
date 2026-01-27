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
import java.io.InputStream
import java.time.LocalDate
import java.util.UUID.randomUUID

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

        val s3Key = generateS3Key(fileType, originalFilename)

        val putReq = PutObjectRequest.builder()
            .bucket(awsProperties.s3.bucket)
            .key(s3Key)
            .contentType(contentType)
            .build()

        file.inputStream.use { input ->
            s3Client.putObject(
                putReq,
                RequestBody.fromInputStream(input, size)
            )
        }

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
