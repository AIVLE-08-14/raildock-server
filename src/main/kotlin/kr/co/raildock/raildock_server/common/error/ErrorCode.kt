package kr.co.raildock.raildock_server.common.error

import org.springframework.http.HttpStatus

interface ErrorCode {
    val status: HttpStatus
    val message: String
    val code: String
}
