package kr.co.raildock.raildock_server.user.dto

import jakarta.validation.constraints.*

data class SignUpRequestDTO(
    @field:Email
    @field:NotBlank
    val email: String,

    @field:Size(min = 8)
    @field:NotBlank
    val password: String,

    @field:NotBlank
    val name: String
)