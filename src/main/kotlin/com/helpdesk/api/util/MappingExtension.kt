package com.helpdesk.api.util

import com.helpdesk.api.dto.CommentResponse
import com.helpdesk.api.dto.TicketResponse
import com.helpdesk.api.dto.UserResponse
import com.helpdesk.api.model.Comment
import com.helpdesk.api.model.Ticket
import com.helpdesk.api.model.User

fun User.toResponse(): UserResponse {
    return UserResponse(
        id = this.id,
        username = this.username,
        email = this.email,
        role = this.role,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

fun Comment.toResponse(): CommentResponse {
    return CommentResponse(
        id = this.id,
        ticketId = this.ticket.id,
        userId = this.user.id,
        username = this.user.username,
        content = this.content,
        createdAt = this.createdAt
    )
}

fun Ticket.toResponse(commentsResponse: List<CommentResponse>): TicketResponse {
    return TicketResponse(
        id = this.id,
        subject = this.subject,
        description = this.description,
        status = this.status,
        priority = this.priority,
        requesterId = this.requester.id,
        requesterUsername = this.requester.username,
        assignedAgentId = this.assignedAgent?.id,
        assignedAgentUsername = this.assignedAgent?.username,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        closedAt = this.closedAt,
        comments = commentsResponse
    )
}