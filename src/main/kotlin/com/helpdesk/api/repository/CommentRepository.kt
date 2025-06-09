package com.helpdesk.api.repository

import com.helpdesk.api.model.Comment
import com.helpdesk.api.model.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface CommentRepository : JpaRepository<Comment, UUID> {
    fun findByTicket(ticket: Ticket): List<Comment>
}