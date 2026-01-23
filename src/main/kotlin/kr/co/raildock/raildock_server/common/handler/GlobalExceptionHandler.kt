package kr.co.raildock.raildock_server.common.handler

import kr.co.raildock.raildock_server.common.ApiResponse
import kr.co.raildock.raildock_server.common.exception.BusinessException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<Unit>> {
        val code = e.errorCode
        return ResponseEntity
            .status(code.status)
            .body(ApiResponse.error(code.code, code.message))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity
            .status(500)
            .body(ApiResponse.error(
                "INTERNAL_SERVER_ERROR",
                "서버 내부 오류"
            ))
    }
}
