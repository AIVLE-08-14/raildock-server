package kr.co.raildock.raildock_server.user.service

import kr.co.raildock.raildock_server.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(
    private val user: User
): UserDetails {
    override fun getUsername(): String = user.email
    override fun getPassword(): String = user.passwordHash
    override fun getAuthorities(): Collection<GrantedAuthority> = listOf()
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    fun getUserId(): Long = user.id

}