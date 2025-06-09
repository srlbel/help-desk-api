package com.helpdesk.api.controller

import com.helpdesk.api.dto.UserCreateRequest
import com.helpdesk.api.dto.UserResponse
import com.helpdesk.api.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@Valid @RequestBody request: UserCreateRequest): UserResponse {
        return userService.reqisterUser(request)
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllUser(): List<UserResponse> {
        return userService.findAllUsers()
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurity.canAccessUser(#id)")
    fun getUserById(@PathVariable id: UUID): UserResponse {
        return userService.findUserById(id)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(@PathVariable id: UUID) {
        userService.deleteUser(id)
    }
}