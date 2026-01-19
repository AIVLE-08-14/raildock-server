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

    val phoneNumber: String,
)