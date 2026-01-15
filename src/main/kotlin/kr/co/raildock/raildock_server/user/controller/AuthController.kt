package kr.co.raildock.raildock_server.user.controller

import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import kr.co.raildock.raildock_server.common.ApiResponse
import kr.co.raildock.raildock_server.user.dto.SignUpRequestDTO
import kr.co.raildock.raildock_server.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userService: UserService
) {
    @PostMapping("/signup")
    @Operation(summary = "회원가입")
    fun signup(@Valid @RequestBody req: SignUpRequestDTO): ResponseEntity<ApiResponse<Unit>> {
        userService.signUp(req)
        return ResponseEntity.ok(ApiResponse.ok("회원가입 완료"))
    }
}