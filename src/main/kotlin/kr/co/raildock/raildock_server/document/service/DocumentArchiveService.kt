package kr.co.raildock.raildock_server.document.service

import jakarta.servlet.http.HttpServletResponse

interface DocumentArchiveService {
    fun downloadAllDocumentsAsZip(response: HttpServletResponse)
}
