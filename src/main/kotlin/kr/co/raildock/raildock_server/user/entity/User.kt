package kr.co.raildock.raildock_server.user.entity

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access
import jakarta.persistence.*


@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true, nullable = false)
    val employeeId: String,

    @Column(unique = true)
    val email: String,

    @Column(unique = true)
    val phoneNumber: String,

    @Column(nullable = false)
    @JsonProperty(access = Access.WRITE_ONLY)
    var passwordHash: String,

    @Column(nullable = false)
    val name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    var role: Role = Role.WORKER
)