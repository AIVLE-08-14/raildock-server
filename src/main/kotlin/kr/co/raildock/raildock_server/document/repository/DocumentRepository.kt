package kr.co.raildock.raildock_server.document.repository

import kr.co.raildock.raildock_server.document.entity.Document
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface DocumentRepository : JpaRepository<Document, UUID>{
    fun existsByName(name: String): Boolean
}
