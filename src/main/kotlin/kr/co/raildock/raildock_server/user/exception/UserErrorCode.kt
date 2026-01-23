package kr.co.raildock.raildock_server.user.exception

import kr.co.raildock.raildock_server.common.error.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val status: HttpStatus,
    override val message: String
) : ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다"),
    DUPLICATE_EMPLOYEE_ID(HttpStatus.CONFLICT, "이미 존재하는 사원번호입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다");

    override val code: String = name
}
