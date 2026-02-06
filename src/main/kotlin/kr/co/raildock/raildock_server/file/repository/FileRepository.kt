package kr.co.raildock.raildock_server.file.repository

import kr.co.raildock.raildock_server.file.entity.FileEntity
import kr.co.raildock.raildock_server.file.enum.FileStatus
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<FileEntity, Long> {

    fun findByParentIdAndOriginalFilenameAndStatus(parentId: Long, originalFilename: String, status: FileStatus): FileEntity?

    fun findByIdAndStatus(
        id: Long,
        status: FileStatus = FileStatus.ACTIVE
    ): FileEntity?

    fun findByS3KeyAndStatus(
        s3Key: String,
        status: FileStatus = FileStatus.ACTIVE
    ): FileEntity?
}
