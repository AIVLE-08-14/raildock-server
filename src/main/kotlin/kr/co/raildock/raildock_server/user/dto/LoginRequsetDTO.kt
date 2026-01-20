package kr.co.raildock.raildock_server.user.dto

import jakarta.validation.constraints.NotBlank

data class LoginRequestDTO(
    @field:NotBlank
    val employeeId: String,
    @field:NotBlank
    val password: String
)
