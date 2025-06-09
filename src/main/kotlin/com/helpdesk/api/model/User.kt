package com.helpdesk.api.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "user")
data class User (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    var username: String,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: UserRole = UserRole.USER,

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)