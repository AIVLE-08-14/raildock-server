package kr.co.raildock.raildock_server.user.dto

import kr.co.raildock.raildock_server.user.entity.Role
import kr.co.raildock.raildock_server.user.entity.User

data class MeResponseDTO(
    val id: Long,
    val employeeId: String,
    val name: String,
    val role: Role
) {
    companion object {
        fun from(user: User): MeResponseDTO {
            return MeResponseDTO(
                id = user.id,
                employeeId = user.employeeId,
                name = user.name,
                role = user.role,
            )
        }
    }
}
