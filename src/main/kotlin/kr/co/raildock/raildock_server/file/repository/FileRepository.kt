package kr.co.raildock.raildock_server.file.repository

import kr.co.raildock.raildock_server.file.entity.FileEntity
import kr.co.raildock.raildock_server.file.entity.FileStatus
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<FileEntity, Long> {

    fun findByIdAndStatus(
        id: Long,
        status: FileStatus = FileStatus.ACTIVE
    ): FileEntity?

    fun findByS3KeyAndStatus(
        s3Key: String,
        status: FileStatus = FileStatus.ACTIVE
    ): FileEntity?
}
