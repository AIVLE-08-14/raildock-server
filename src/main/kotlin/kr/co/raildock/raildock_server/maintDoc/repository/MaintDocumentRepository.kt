package kr.co.raildock.raildock_server.maintDoc.repository

import kr.co.raildock.raildock_server.maintDoc.entity.MaintDocument
import org.springframework.data.jpa.repository.JpaRepository

interface MaintDocumentRepository: JpaRepository<MaintDocument, Long>{
}