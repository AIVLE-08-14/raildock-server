package kr.co.raildock.raildock_server.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.co.raildock.raildock_server.common.ApiResponse
import kr.co.raildock.raildock_server.user.dto.LoginRequestDTO
import kr.co.raildock.raildock_server.user.dto.MeResponseDTO
import kr.co.raildock.raildock_server.user.dto.SignUpRequestDTO
import kr.co.raildock.raildock_server.user.service.UserPrincipal
import kr.co.raildock.raildock_server.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.web.bind.annotation.*

@Tag(
    name = "Auth",
    description = "세션 기반 사용자 인증 API (회원가입, 로그인, 로그아웃, 마이프로필)"
)
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
        return ResponseEntity.ok(ApiResponse.success("회원가입 완료"))
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 - 사원번호(employeeId)가 ID")
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

        return ResponseEntity.ok(ApiResponse.success("로그인 성공"))
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    fun logout(
        request: HttpServletRequest
    ): ResponseEntity<ApiResponse<Unit>> {
        SecurityContextHolder.clearContext()
        request.getSession(false)?.invalidate()
        return ResponseEntity.ok(ApiResponse.success("로그아웃 성공"))
    }

    @GetMapping("/me")
    @Operation(summary = "마이프로필 조회")
    fun me(
        @AuthenticationPrincipal principal: UserPrincipal
    ): ResponseEntity<ApiResponse<MeResponseDTO>?> {
        val user = userService.getUser(principal.userId)
        return ResponseEntity.ok(ApiResponse.success(MeResponseDTO.from(user)))
    }

}