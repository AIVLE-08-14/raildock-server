package kr.co.raildock.raildock_server.user.service

import jakarta.servlet.http.HttpServletRequest
import kr.co.raildock.raildock_server.common.exception.BusinessException
import kr.co.raildock.raildock_server.user.dto.LoginRequestDTO
import kr.co.raildock.raildock_server.user.dto.PasswordChangeRequestDTO
import kr.co.raildock.raildock_server.user.dto.SignUpRequestDTO
import kr.co.raildock.raildock_server.user.dto.UserUpdateRequestDTO
import kr.co.raildock.raildock_server.user.entity.User
import kr.co.raildock.raildock_server.user.exception.UserErrorCode
import kr.co.raildock.raildock_server.user.repository.UserRepository
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService (
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
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

    @Transactional
    fun login(req: LoginRequestDTO, request: HttpServletRequest) {
        try {
            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                    req.employeeId,
                    req.password
                )
            )

            val context = SecurityContextHolder.createEmptyContext()
            context.authentication = authentication

            request.session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                context
            )

        } catch (e: Exception) {
            // Spring Security 인증 실패는 전부 로그인 실패로 변환
            throw BusinessException(UserErrorCode.INVALID_LOGIN)
        }
    }

    @Transactional(readOnly = true)
    fun getUser(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow {
                BusinessException(UserErrorCode.USER_NOT_FOUND)
            }
    }

    @Transactional
    fun updateMe(userId: Long, req: UserUpdateRequestDTO) {
        val user = userRepository.findById(userId)
            .orElseThrow {
                BusinessException(UserErrorCode.USER_NOT_FOUND)
            }

        req.name?.let {
            user.name = it
        }

        req.email?.let {
            if (it != user.email && userRepository.existsByEmail(it)) {
                throw BusinessException(UserErrorCode.DUPLICATE_EMAIL)
            }
            user.email = it
        }

        req.phoneNumber?.let {
            user.phoneNumber = it
        }
    }

    @Transactional
    fun deleteMe(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow {
                BusinessException(UserErrorCode.USER_NOT_FOUND)
            }

        userRepository.delete(user)
    }

    @Transactional
    fun changePassword(
        userId: Long,
        req: PasswordChangeRequestDTO
    ) {
        val user = userRepository.findById(userId)
            .orElseThrow {
                BusinessException(UserErrorCode.USER_NOT_FOUND)
            }

        // 1️⃣ 현재 비밀번호 검증
        if (!passwordEncoder.matches(req.currentPassword, user.passwordHash)) {
            throw BusinessException(UserErrorCode.INVALID_PASSWORD)
        }

        // 2️⃣ 기존 비밀번호와 동일한지 체크
        if (passwordEncoder.matches(req.newPassword, user.passwordHash)) {
            throw BusinessException(UserErrorCode.SAME_AS_OLD_PASSWORD)
        }

        // 3️⃣ 새 비밀번호 암호화 후 저장
        user.passwordHash = passwordEncoder.encode(req.newPassword)
    }

    @Transactional(readOnly = true)
    fun getMyId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw BusinessException(UserErrorCode.UNAUTHORIZED)

        val principal = authentication.principal

        if (principal is UserPrincipal) {
            return principal.userId
        }

        throw BusinessException(UserErrorCode.UNAUTHORIZED)
    }
}