package kr.co.raildock.raildock_server.file.entity

import jakarta.persistence.*
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "files")
class FileEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val s3Key: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val fileType: FileType,

    @Column(nullable = false)
    val contentType: String,

    @Column(nullable = false)
    val size: Long,

    @Column(nullable = false)
    val originalFilename: String,

    @Column(nullable = false)
    val bucket: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: FileStatus = FileStatus.ACTIVE,

    @CreationTimestamp
    @Column(updatable = false)
    val createdAt: LocalDateTime? = null
) {
    fun markDeleted() {
        this.status = FileStatus.DELETED
    }
}

enum class FileType {
    IMAGE, VIDEO, JSON
}

enum class FileStatus {
    ACTIVE, DELETED
}
