package kr.co.raildock.raildock_server.common

data class ApiResponse<T>(
    val success: Boolean,
    val code: String? = null,
    val message: String,
    val data: T? = null
) {
    companion object {

        // ===== 성공 =====
        fun success(): ApiResponse<Unit> =
            ApiResponse(
                success = true,
                message = "OK"
            )

        fun success(message: String): ApiResponse<Unit> =
            ApiResponse(
                success = true,
                message = message
            )

        fun <T> success(data: T): ApiResponse<T> =
            ApiResponse(
                success = true,
                message = "OK",
                data = data
            )

        fun <T> success(
            data: T,
            message: String
        ): ApiResponse<T> =
            ApiResponse(
                success = true,
                message = message,
                data = data
            )

        // ===== 실패 =====
        fun error(
            code: String,
            message: String
        ): ApiResponse<Unit> =
            ApiResponse(
                success = false,
                code = code,
                message = message
            )
    }
}