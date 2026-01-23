package kr.co.raildock.raildock_server.user.exception

import kr.co.raildock.raildock_server.common.error.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val status: HttpStatus,
    override val message: String
) : ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다"),
    DUPLICATE_EMPLOYEE_ID(HttpStatus.CONFLICT, "이미 존재하는 사원번호입니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 존재하는 이메일입니다"),
    INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "사원번호 또는 비밀번호가 올바르지 않습니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "현재 비밀번호가 일치하지 않습니다"),
    SAME_AS_OLD_PASSWORD(HttpStatus.BAD_REQUEST, "기존 비밀번호와 동일합니다");

    override val code: String = name
}
