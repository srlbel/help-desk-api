package com.helpdesk.api.dto

import com.helpdesk.api.model.TicketPriority
import com.helpdesk.api.model.TicketStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class TicketCreateRequest(
    @field:NotBlank(message = "Subject cannot be empty")
    @field:Size(min = 5, max = 255, message = "Subject must be between 5 and 255 characters")
    val subject: String,

    @field:NotBlank(message = "Description cannot be empty")
    @field:Size(min = 10, message = "Description must be at least 10 characters long")
    val description: String,

    val priority: TicketPriority = TicketPriority.MEDIUM
)

data class TicketUpdateRequest(
    @field:Size(min = 5, max = 255, message = "Subject must be between 5 and 255 characters")
    val subject: String?,

    @field:Size(min = 10, message = "Description must be at least 10 characters long")
    val description: String?,

    val status: TicketStatus?,
    val priority: TicketPriority?,
    val assignedAgentId: UUID?
)

data class TicketResponse(
    val id: UUID,
    val subject: String,
    val description: String,
    val status: TicketStatus,
    val priority: TicketPriority,
    val requesterId: UUID,
    val requesterUsername: String,
    val assignedAgentId: UUID?,
    val assignedAgentUsername: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val closedAt: LocalDateTime?,
    val comments: List<CommentResponse>
)