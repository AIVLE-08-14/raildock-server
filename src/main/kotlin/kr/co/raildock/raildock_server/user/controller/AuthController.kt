package kr.co.raildock.raildock_server.user.controller

import io.swagger.v3.oas.annotations.Operation
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.co.raildock.raildock_server.common.ApiResponse
import kr.co.raildock.raildock_server.user.dto.LoginRequestDTO
import kr.co.raildock.raildock_server.user.dto.MeResponseDTO
import kr.co.raildock.raildock_server.user.dto.SignUpRequestDTO
import kr.co.raildock.raildock_server.user.entity.CustomUserDetails
import kr.co.raildock.raildock_server.user.service.UserPrincipal
import kr.co.raildock.raildock_server.user.service.UserService
import org.springframework.context.annotation.Bean
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager
) {

    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    fun signup(
        @Valid @RequestBody req: SignUpRequestDTO
    ): ResponseEntity<ApiResponse<Unit>> {
        userService.signUp(req)
        return ResponseEntity.ok(ApiResponse.ok("회원가입 완료"))
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody req: LoginRequestDTO,
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {

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

        return ResponseEntity.ok(ApiResponse.ok("로그인 성공"))
    }

@GetMapping("/me")
    fun me(
        @AuthenticationPrincipal principal: UserPrincipal
    ): ResponseEntity<ApiResponse<MeResponseDTO>?> {
        val user = userService.getUser(principal.userId)
        return ResponseEntity.ok(ApiResponse.ok(MeResponseDTO.from(user)))
    }

}