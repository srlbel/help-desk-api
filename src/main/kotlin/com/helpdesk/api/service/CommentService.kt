package com.helpdesk.api.service

import com.helpdesk.api.dto.CommentCreateRequest
import com.helpdesk.api.dto.CommentResponse
import com.helpdesk.api.exception.ResourceNotFoundException
import com.helpdesk.api.model.Comment
import com.helpdesk.api.model.Ticket
import com.helpdesk.api.repository.CommentRepository
import com.helpdesk.api.util.toResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val ticketService: TicketService,
    private val userService: UserService
) {

    @Transactional
    fun addCommentToTicket(ticketId: UUID, userId: UUID, request: CommentCreateRequest): CommentResponse {
        val ticket = ticketService.findTicketEntityById(ticketId)
        val user = userService.findUserEntityById(userId)

        val newComment = Comment(
            ticket = ticket,
            user = user,
            content = request.content,
            createdAt = LocalDateTime.now()
        )
        val savedComment = commentRepository.save(newComment)
        ticket.comments.add(savedComment)
        return savedComment.toResponse()
    }

    fun findCommentsByTicket(ticketId: UUID): List<CommentResponse> {
        val ticket = ticketService.findTicketEntityById(ticketId)
        return commentRepository.findByTicket(ticket).map { it.toResponse() }
    }

    fun findCommentsByTicketEntity(ticket: Ticket): List<Comment> {
        return commentRepository.findByTicket(ticket)
    }

    fun findCommentById(id: UUID): CommentResponse {
        return commentRepository.findById(id).orElseThrow { ResourceNotFoundException("Comment not found with id: $id") }.toResponse()
    }

    @Transactional
    fun deleteComment(id: UUID) {
        if (!commentRepository.existsById(id)) {
            throw ResourceNotFoundException("Comment not found with id: $id")
        }
        commentRepository.deleteById(id)
    }
}