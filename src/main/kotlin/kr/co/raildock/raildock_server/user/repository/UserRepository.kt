package kr.co.raildock.raildock_server.user.repository

import kr.co.raildock.raildock_server.user.entity.User
import org.springframework.boot.autoconfigure.security.SecurityProperties
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByEmployeeId(employeeId: String): User?
}