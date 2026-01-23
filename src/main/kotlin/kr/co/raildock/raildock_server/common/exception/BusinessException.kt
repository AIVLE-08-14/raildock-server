package kr.co.raildock.raildock_server.common.exception

import kr.co.raildock.raildock_server.common.error.ErrorCode

open class BusinessException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
