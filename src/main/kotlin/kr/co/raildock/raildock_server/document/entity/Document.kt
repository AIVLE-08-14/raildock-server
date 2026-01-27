package kr.co.raildock.raildock_server.document.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "documents")
class Document(

    @Id
    @GeneratedValue
    val id: UUID? = null,

    @Column(nullable = false , unique = true)
    var name: String,

    @Column(nullable = false)
    var description: String,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)
