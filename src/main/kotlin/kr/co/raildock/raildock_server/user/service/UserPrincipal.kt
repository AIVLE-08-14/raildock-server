package kr.co.raildock.raildock_server.user.service

import kr.co.raildock.raildock_server.user.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.io.Serializable

class UserPrincipal(
    val userId: Long,
    private val employeeId: String,
    private val passwordHash: String
): UserDetails, Serializable {
    override fun getUsername(): String = employeeId
    override fun getPassword(): String = passwordHash
    override fun getAuthorities(): Collection<GrantedAuthority> = emptyList()
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

}