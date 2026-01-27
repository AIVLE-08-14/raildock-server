package kr.co.raildock.raildock_server.document.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "document_revisions",
    uniqueConstraints = [
        jakarta.persistence.UniqueConstraint(
            columnNames = ["document_id", "revision_version"]
        )
    ]
) // document_id와 revision_version의 충돌 방지
class DocumentRevision(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    val document: Document,

    @Column(nullable = false)
    val revisionVersion: Int,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val createdBy: Long,

    @Column(nullable = false)
    val fileId: Long
)
