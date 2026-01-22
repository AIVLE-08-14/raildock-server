package kr.co.raildock.raildock_server.file.service

import kr.co.raildock.raildock_server.config.S3Properties
import kr.co.raildock.raildock_server.file.dto.UploadFileResponse
import kr.co.raildock.raildock_server.file.entity.FileEntity
import kr.co.raildock.raildock_server.file.entity.FileType
import kr.co.raildock.raildock_server.file.repository.FileRepository
import org.springframework.core.io.Resource
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileServiceImpl(
    private val fileRepository: FileRepository,
    private val s3Client: software.amazon.awssdk.services.s3.S3Client,
    private val s3Properties: S3Properties
) : FileService {

    override fun upload(file: MultipartFile, fileType: FileType): UploadFileResponse {
        val originalFilename = file.originalFilename ?: "unknown"
        val contentType = file.contentType ?: "application/octet-stream"
        val size = file.size

        val s3Key = generateS3Key(fileType, originalFilename)

        val putReq = software.amazon.awssdk.services.s3.model.PutObjectRequest.builder()
            .bucket(s3Properties.bucket)
            .key(s3Key)
            .contentType(contentType)
            .build()

        // 스트리밍 업로드 (메모리 폭발 방지)
        file.inputStream.use { input ->
            s3Client.putObject(
                putReq,
                software.amazon.awssdk.core.sync.RequestBody.fromInputStream(input, size)
            )
        }

        val saved = fileRepository.save(
            FileEntity(
                s3Key = s3Key,
                fileType = fileType,
                contentType = contentType,
                size = size,
                originalFilename = originalFilename,
                bucket = s3Properties.bucket
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

    override fun download(fileId: Long): ResponseEntity<Resource> {
        val file = fileRepository.findByIdAndStatus(fileId) ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")

        val getReq = software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
            .bucket(file.bucket)
            .key(file.s3Key)
            .build()

        val s3Object = s3Client.getObject(getReq) // ResponseInputStream<GetObjectResponse>

        val resource = org.springframework.core.io.InputStreamResource(s3Object)

        return ResponseEntity.ok()
            .header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"${file.originalFilename}\"")
            .contentType(org.springframework.http.MediaType.parseMediaType(file.contentType))
            .contentLength(file.size)
            .body(resource)
    }

    override fun deleteFile(fileId: Long) {
        val file = fileRepository.findByIdAndStatus(fileId) ?: throw IllegalArgumentException("파일이 존재하지 않습니다.")
        file.markDeleted()
        fileRepository.save(file) // 또는 트랜잭션 + 더티체킹이면 save 생략 가능
    }

    private fun generateS3Key(fileType: FileType, originalFilename: String): String {
        val ext = originalFilename.substringAfterLast('.', "")
        val date = java.time.LocalDate.now()
        val uuid = java.util.UUID.randomUUID()
        return "${fileType.name.lowercase()}/$date/$uuid.${ext}"
    }
}