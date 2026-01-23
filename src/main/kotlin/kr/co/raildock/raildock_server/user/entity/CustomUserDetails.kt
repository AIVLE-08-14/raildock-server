package kr.co.raildock.raildock_server.user.entity

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    val user: User
) : UserDetails {

    // 로그인 ID (username 역할)
    override fun getUsername(): String =
        user.employeeId

    // 암호화된 비밀번호
    override fun getPassword(): String =
        user.passwordHash

    // 권한 목록 (ROLE_ prefix 필수)
    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))

    // 계정 만료 여부
    override fun isAccountNonExpired(): Boolean = true

    // 계정 잠김 여부
    override fun isAccountNonLocked(): Boolean = true

    // 자격 증명 만료 여부
    override fun isCredentialsNonExpired(): Boolean = true

    // 계정 활성화 여부
    override fun isEnabled(): Boolean = true
}
