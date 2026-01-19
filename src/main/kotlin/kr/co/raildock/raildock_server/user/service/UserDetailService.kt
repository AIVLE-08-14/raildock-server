package kr.co.raildock.raildock_server.user.service

import kr.co.raildock.raildock_server.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class UserDetailService(
    private val userRepository: UserRepository
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmployeeId(username)
            ?: throw UsernameNotFoundException("User not found with Employee ID: $username")
        return UserPrincipal(
            userId = user.id,
            employeeId = user.employeeId,
            passwordHash = user.passwordHash
        )
    }

}