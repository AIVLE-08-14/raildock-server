package kr.co.raildock.raildock_server.maintDoc.service

import kr.co.raildock.raildock_server.maintDoc.dto.MaintDocumentCreateRequestDTO
import kr.co.raildock.raildock_server.maintDoc.dto.MaintDocumentResponseDTO
import kr.co.raildock.raildock_server.maintDoc.dto.MaintDocumentUpdateRequestDTO
import kr.co.raildock.raildock_server.maintDoc.entity.MaintDocument
import kr.co.raildock.raildock_server.maintDoc.repository.MaintDocumentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MaintDocumentService(
    private val maintDocumentRepository: MaintDocumentRepository
) {

    @Transactional
    fun create(req: MaintDocumentCreateRequestDTO): MaintDocumentResponseDTO{
        val entity = MaintDocument(
            title = req.title.trim(),
            content = req.content,
            category = (req.category ?: "GENERAL").trim()
        )
        return MaintDocumentResponseDTO.from(maintDocumentRepository.save(entity))
    }

    @Transactional(readOnly = true)
    fun get(id: Long): MaintDocumentResponseDTO{
        val entity = maintDocumentRepository.findById(id).orElseThrow{
            NoSuchElementException("No maint document found with ID $id")
        }
        return MaintDocumentResponseDTO.from(entity)
    }

    @Transactional(readOnly = true)
    fun list(page: Int, size: Int): Page<MaintDocumentResponseDTO> {
        val pageable = PageRequest.of(
            page.coerceAtLeast(0),
            size.coerceIn(1, 50),
            Sort.by(Sort.Direction.DESC, "updatedAt")
        )
        return maintDocumentRepository.findAll(pageable).map{ MaintDocumentResponseDTO.from(it) }
    }

    @Transactional
    fun update(id: Long, req: MaintDocumentUpdateRequestDTO): MaintDocumentResponseDTO{
        val entity = maintDocumentRepository.findById(id).orElseThrow{
            NoSuchElementException("No maint document found with ID $id")
        }
        req.title?.let { entity.title = it.trim() }
        req.content?.let { entity.content = it }
        req.category?.let { entity.category = it.trim() }

        return MaintDocumentResponseDTO.from(entity)
    }

    @Transactional
    fun delete(id: Long){
        if(!maintDocumentRepository.existsById(id)){
            throw NoSuchElementException("Document not found with ID $id")
        }
        maintDocumentRepository.deleteById(id)
    }
}