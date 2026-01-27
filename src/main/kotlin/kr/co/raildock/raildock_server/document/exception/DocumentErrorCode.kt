package kr.co.raildock.raildock_server.document.exception

import kr.co.raildock.raildock_server.common.error.ErrorCode
import org.springframework.http.HttpStatus

enum class DocumentErrorCode(
    override val status: HttpStatus,
    override val message: String
) : ErrorCode {

    DOCUMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 유지보수 문서입니다"),
    DUPLICATE_DOCUMENT_NAME(HttpStatus.CONFLICT, "이미 존재하는 문서 이름입니다"),
    DOCUMENT_HAS_NO_REVISION(HttpStatus.BAD_REQUEST, "해당 문서에는 아직 개정 이력이 없습니다"),
    ONLY_LATEST_REVISION_CAN_BE_DELETED(HttpStatus.BAD_REQUEST, "개정 이력 삭제는 최신 버전만 가능합니다"),
    INVALID_FILE_TYPE(HttpStatus.BAD_REQUEST, "PDF 파일만 업로드할 수 있습니다");

    override val code: String = name
}
