package com.helpdesk.api.controller

import com.helpdesk.api.config.CustomUserDetails
import com.helpdesk.api.config.UserSecurity
import com.helpdesk.api.dto.CommentCreateRequest
import com.helpdesk.api.dto.CommentResponse
import com.helpdesk.api.service.CommentService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/tickets/{ticketId}/comments")
class CommentController(
    private val commentService: CommentService,
    private val userSecurity: UserSecurity
) {

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT') or @userSecurity.canAccessTicket(#ticketId, principal)")
    @ResponseStatus(HttpStatus.CREATED)
    fun addComment(
        @PathVariable ticketId: UUID,
        @Valid @RequestBody request: CommentCreateRequest,
        @AuthenticationPrincipal currentUser: CustomUserDetails
    ): CommentResponse {
        return commentService.addCommentToTicket(ticketId, currentUser.id, request)
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT') or @userSecurity.canAccessTicket(#ticketId, principal)")
    fun getCommentsForTicket(@PathVariable ticketId: UUID): List<CommentResponse> { // Using Kotlin's List
        return commentService.findCommentsByTicket(ticketId)
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT') or @userSecurity.isCommentAuthor(#commentId, principal)")
    fun deleteComment(@PathVariable ticketId: UUID, @PathVariable commentId: UUID) {
        // Optional: Add a check here if commentId truly belongs to ticketId for stricter validation
        commentService.deleteComment(commentId)
    }
}