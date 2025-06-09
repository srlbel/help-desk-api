package com.helpdesk.api.repository

import com.helpdesk.api.model.User
import java.util.UUID
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<User, UUID> {
    fun findByUsername(username: String): Optional<User>
    fun findByEmail(email: String): Optional<User>
}