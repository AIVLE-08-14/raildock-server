package kr.co.raildock.raildock_server.user.service

import kr.co.raildock.raildock_server.user.dto.SignUpRequestDTO
import kr.co.raildock.raildock_server.user.entity.User
import kr.co.raildock.raildock_server.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
){
    @Transactional
    fun signUp(req: SignUpRequestDTO){
        // TODO: Email 중복 체크

        val user = userRepository.save(
            User(
                email = req.email,
                passwordHash = passwordEncoder.encode(req.password),
                name = req.name,
            )
        )
    }

}