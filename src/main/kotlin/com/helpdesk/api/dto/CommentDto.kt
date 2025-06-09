package com.helpdesk.api.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

data class CommentCreateRequest(
    @field:NotBlank(message = "Comment content cannot be empty")
    @field:Size(min = 5, max = 1000, message = "Comment must be between 5 and 1000 characters")
    val content: String
)

data class CommentResponse(
    val id: UUID,
    val ticketId: UUID,
    val userId: UUID,
    val username: String,
    val content: String,
    val createdAt: LocalDateTime
)