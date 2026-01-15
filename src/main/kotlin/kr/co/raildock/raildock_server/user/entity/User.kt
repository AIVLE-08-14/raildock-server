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

    @Column(nullable = false, unique = true)
    val email: String,

    @Column(nullable = false)
    @JsonProperty(access = Access.WRITE_ONLY)
    var passwordHash: String,

    @Column(nullable = false)
    val name: String,

    @Column(name = "role")
    var roles: MutableSet<String> = mutableSetOf("USER")
)