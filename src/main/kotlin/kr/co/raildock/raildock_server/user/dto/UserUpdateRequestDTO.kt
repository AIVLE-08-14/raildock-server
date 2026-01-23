package kr.co.raildock.raildock_server.user.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class UserUpdateRequestDTO(

    val name: String?,

    @field:Email
    val email: String?,

    @field:Pattern(
        regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$",
        message = "휴대폰 번호 형식이 올바르지 않습니다"
    )
    val phoneNumber: String?
)
