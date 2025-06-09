package com.helpdesk.api.dto

import com.helpdesk.api.model.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class UserCreateRequest (
    @field:NotBlank(message = "Username cannot be empty")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,

    @field:NotBlank(message = "Email cannot be empty")
    @field:Email(message = "Invalid email format")
    val email: String,

    @field:NotBlank(message = "Password cannot be empty")
    @field:Size(min = 6, message = "Password must be at least 6 characters long")
    val passwordPlain: String,

    val role: UserRole? = UserRole.USER
    )

data class UserUpdateRequest(
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String?,

    @field:Email(message = "Invalid email format")
    val email: String?,

    val role: UserRole?
)

data class UserResponse(
    val id: UUID,
    val username: String,
    val email: String,
    val role: UserRole,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)