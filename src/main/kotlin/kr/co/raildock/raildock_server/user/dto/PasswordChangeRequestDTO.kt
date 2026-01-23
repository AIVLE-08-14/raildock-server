package kr.co.raildock.raildock_server.user.dto

import jakarta.validation.constraints.NotBlank

data class PasswordChangeRequestDTO(

    @field:NotBlank
    val currentPassword: String,

    @field:NotBlank
    val newPassword: String
)
