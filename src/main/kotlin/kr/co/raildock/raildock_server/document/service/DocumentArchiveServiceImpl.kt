package kr.co.raildock.raildock_server.document.service

import jakarta.servlet.http.HttpServletResponse
import kr.co.raildock.raildock_server.document.repository.DocumentRepository
import kr.co.raildock.raildock_server.document.repository.DocumentRevisionRepository
import kr.co.raildock.raildock_server.file.service.FileService
import org.springframework.stereotype.Service
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

@Service
class DocumentArchiveServiceImpl(
    private val documentRepository: DocumentRepository,
    private val revisionRepository: DocumentRevisionRepository,
    private val fileService: FileService
) : DocumentArchiveService {

    override fun downloadAllDocumentsAsZip(response: HttpServletResponse) {

        response.contentType = "application/zip"
        response.setHeader(
            "Content-Disposition",
            "attachment; filename=documents.zip"
        )

        ZipOutputStream(response.outputStream).use { zipOut ->

            val documents = documentRepository.findAll()

            documents.forEach { document ->
                val revisions =
                    revisionRepository.findByDocumentIdOrderByRevisionVersionDesc(document.id!!)

                revisions.forEach { revision ->
                    val entryName =
                        "${document.name}/v${revision.revisionVersion}.pdf"

                    zipOut.putNextEntry(ZipEntry(entryName))

                    // 🔥 S3 스트리밍 → ZIP 스트리밍
                    fileService.openStream(revision.fileId).use { input ->
                        input.copyTo(zipOut)
                    }

                    zipOut.closeEntry()
                }
            }
        }
    }
}
