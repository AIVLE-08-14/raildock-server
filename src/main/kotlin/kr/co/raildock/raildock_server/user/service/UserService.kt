package kr.co.raildock.raildock_server.user.service

import kr.co.raildock.raildock_server.common.exception.BusinessException
import kr.co.raildock.raildock_server.user.dto.SignUpRequestDTO
import kr.co.raildock.raildock_server.user.entity.User
import kr.co.raildock.raildock_server.user.exception.UserErrorCode
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
        // 사원번호 중복 체크
        if (userRepository.existsByEmployeeId(req.employeeId)) {
            throw BusinessException(UserErrorCode.DUPLICATE_EMPLOYEE_ID)
        }

        // 이메일 중복 체크
        if (userRepository.existsByEmail(req.email)) {
            throw BusinessException(UserErrorCode.DUPLICATE_EMAIL)
        }

        userRepository.save(
            User(
                employeeId = req.employeeId,
                passwordHash = passwordEncoder.encode(req.password),
                email = req.email,
                phoneNumber = req.phoneNumber,
                name = req.name,
            )
        )
    }

    @Transactional(readOnly = true)
    fun getUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { RuntimeException("존재하지 않는 사용자") }
    }
}