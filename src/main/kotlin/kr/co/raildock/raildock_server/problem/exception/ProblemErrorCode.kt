package kr.co.raildock.raildock_server.problem.exception

import kr.co.raildock.raildock_server.common.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ProblemErrorCode(
    override val status: HttpStatus,
    override val message: String
) : ErrorCode {

    PROBLEM_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 결함(Problem)입니다"),
    INVALID_PROBLEM_STATUS(HttpStatus.BAD_REQUEST, "유효하지 않은 결함 상태입니다"),
    INVALID_PROBLEM_SEVERITY(HttpStatus.BAD_REQUEST, "유효하지 않은 결함 심각도입니다"),
    PROBLEM_ALREADY_ASSIGNED(HttpStatus.CONFLICT, "이미 담당자가 할당된 결함입니다"),
    PROBLEM_NOT_ASSIGNED(HttpStatus.BAD_REQUEST, "아직 담당자가 할당되지 않은 결함입니다"),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "결함 상태를 해당 단계로 변경할 수 없습니다"),
    INVALID_LOCATION(HttpStatus.BAD_REQUEST, "결함 위치 정보가 올바르지 않습니다");

    override val code: String = name
}
