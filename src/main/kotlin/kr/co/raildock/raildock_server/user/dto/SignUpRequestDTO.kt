package kr.co.raildock.raildock_server.user.dto

import jakarta.validation.constraints.*

data class SignUpRequestDTO(
    @field:NotBlank
    val employeeId: String,

    @field:Size(min = 8)
    @field:NotBlank
    val password: String,

    @field:NotBlank
    val name: String,

    @field:Email
    val email: String,

    @field:NotBlank
    @field:Pattern(
        regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
        message = "휴대폰 번호 형식이 올바르지 않습니다"
    )
    val phoneNumber: String,
)