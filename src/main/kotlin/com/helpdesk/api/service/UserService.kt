package com.helpdesk.api.service

import com.helpdesk.api.dto.UserCreateRequest
import com.helpdesk.api.dto.UserResponse
import com.helpdesk.api.dto.UserUpdateRequest
import com.helpdesk.api.exception.ResourceNotFoundException
import com.helpdesk.api.model.User
import com.helpdesk.api.model.UserRole
import com.helpdesk.api.repository.UserRepository
import com.helpdesk.api.util.toResponse
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @Transactional
    fun registerUser(request: UserCreateRequest): UserResponse {
        if (userRepository.findByUsername(request.username).isPresent) {
            throw IllegalArgumentException("Username '${request.username}' already exists")
        }
        if (userRepository.findByEmail(request.email).isPresent) {
            throw IllegalArgumentException("Email '${request.email}' already exists")
        }

        val hashedPassword = passwordEncoder.encode(request.passwordPlain)
        val newUser = User(
            username = request.username,
            email = request.email,
            passwordHash = hashedPassword,
            role = request.role ?: UserRole.USER,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        return userRepository.save(newUser).toResponse()
    }

    @Transactional
    fun updateUser(id: UUID, request: UserUpdateRequest): UserResponse {
        val user = userRepository.findById(id).orElseThrow { ResourceNotFoundException("User not found with id: $id") }

        request.username?.let {
            if (it != user.username && userRepository.findByUsername(it).isPresent) {
                throw IllegalArgumentException("Username '${it}' already exists")
            }
            user.username = it
        }
        request.email?.let {
            if (it != user.email && userRepository.findByEmail(it).isPresent) {
                throw IllegalArgumentException("Email '${it}' already exists")
            }
            user.email = it
        }
        request.role?.let { user.role = it }

        user.updatedAt = LocalDateTime.now()
        return userRepository.save(user).toResponse()
    }

    fun findUserById(id: UUID): UserResponse {
        return userRepository.findById(id).orElseThrow { ResourceNotFoundException("User not found with id: $id") }.toResponse()
    }

    fun findUserEntityById(id: UUID): User {
        return userRepository.findById(id).orElseThrow { ResourceNotFoundException("User not found with id: $id") }
    }

    fun findUserByUsername(username: String): User {
        return userRepository.findByUsername(username).orElseThrow { ResourceNotFoundException("User not found with username: $username") }
    }

    fun findAllUsers(): List<UserResponse> { // Using Kotlin's List
        return userRepository.findAll().map { it.toResponse() }
    }

    @Transactional
    fun deleteUser(id: UUID) {
        if (!userRepository.existsById(id)) {
            throw ResourceNotFoundException("User not found with id: $id")
        }
        userRepository.deleteById(id)
    }
}