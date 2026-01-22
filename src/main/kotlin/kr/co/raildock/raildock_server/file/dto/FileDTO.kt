package kr.co.raildock.raildock_server.file.dto

import kr.co.raildock.raildock_server.file.entity.FileType

data class UploadFileResponse(
    val fileId: Long,
    val s3Key: String,
    val bucket: String,
    val originalFilename: String,
    val contentType: String,
    val size: Long
)

data class GenerateUploadUrlRequest(
    val fileType: FileType,
    val contentType: String,
    val size: Long,
    val originalFilename: String
)

data class GenerateUploadUrlResponse(
    val fileId: Long,
    val uploadUrl: String,
    val s3Key: String
)