package kr.co.raildock.raildock_server.maintDoc.entity

import jakarta.persistence.*
import java.time.Instant
import java.time.LocalDateTime

@Entity
@Table(name = "maint_documents")
class MaintDocument(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, length = 200)
    var title: String,

    @Lob
    @Column(nullable = false)
    var content: String,

    @Column(nullable = false, length = 100)
    var category: String = "GENERAL",

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),
){
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }
}