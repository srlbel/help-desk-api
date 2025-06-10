package com.helpdesk.api.config

import com.helpdesk.api.model.UserRole
import com.helpdesk.api.repository.CommentRepository
import com.helpdesk.api.repository.TicketRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * A custom security helper component used in Spring Security's @PreAuthorize expressions.
 * It provides methods to check if an authenticated user has permission to access specific resources (users, tickets, comments).
 *
 * Annotated with `@Component("userSecurity")` to make it a Spring Bean accessible by its name in SpEL expressions.
 *
 * @param ticketRepository Used to fetch ticket details for access checks.
 * @param commentRepository Used to fetch comment details for access checks.
 */
@Component("userSecurity")
class UserSecurity(
    private val ticketRepository: TicketRepository,
    private val commentRepository: CommentRepository
) {

    /**
     * Checks if the currently authenticated user can access/modify a [User] resource by its [userId].
     *
     * Rules:
     * - An ADMIN can access any user.
     * - A regular user can only access their own user details.
     *
     * @param userId The UUID of the user resource being accessed.
     * @return `true` if the user is authorized, `false` otherwise.
     */
    fun canAccessUser(userId: UUID): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal

        // Ensure the principal is our CustomUserDetails type
        if (principal !is CustomUserDetails) {
            return false
        }

        // Check if the current user is an ADMIN or if their ID matches the requested userId
        return principal.id == userId || principal.user.role == UserRole.ADMIN
    }

    /**
     * Checks if the currently authenticated user can access/modify a [Ticket] resource by its [ticketId].
     *
     * Rules:
     * - An ADMIN or AGENT can access any ticket.
     * - A regular user can only access tickets where they are the requester or the assigned agent.
     *
     * @param ticketId The UUID of the ticket resource being accessed.
     * @param principalObject The principal object from Spring Security context.
     * @return `true` if the user is authorized, `false` otherwise.
     */
    fun canAccessTicket(ticketId: UUID, principalObject: Any?): Boolean {
        // Ensure the principal is our CustomUserDetails type
        if (principalObject !is CustomUserDetails) {
            return false
        }

        // Admins and Agents have full access
        if (principalObject.user.role == UserRole.ADMIN || principalObject.user.role == UserRole.AGENT) {
            return true
        }

        // For regular users, check if they are the requester or assigned agent
        val ticket = ticketRepository.findById(ticketId).orElse(null)
        if (ticket == null) {
            return false // Ticket not found, so no access
        }
        return ticket.requester.id == principalObject.id || ticket.assignedAgent?.id == principalObject.id
    }

    /**
     * Checks if the currently authenticated user is the author of a specific [Comment] by its [commentId].
     *
     * Rules:
     * - An ADMIN or AGENT can delete any comment.
     * - The comment author can delete their own comment.
     *
     * @param commentId The UUID of the comment resource being accessed.
     * @param principalObject The principal object from Spring Security context.
     * @return `true` if the user is authorized, `false` otherwise.
     */
    fun isCommentAuthor(commentId: UUID, principalObject: Any?): Boolean {
        // Ensure the principal is our CustomUserDetails type
        if (principalObject !is CustomUserDetails) {
            return false
        }
        // Admins and Agents have full deletion rights on comments
        if (principalObject.user.role == UserRole.ADMIN || principalObject.user.role == UserRole.AGENT) {
            return true
        }

        // Check if the current user is the author of the comment
        val comment = commentRepository.findById(commentId).orElse(null)
        if (comment == null) {
            return false // Comment not found
        }
        return comment.user.id == principalObject.id
    }

    /**
     * Convenience method to check if the current user has either AGENT or ADMIN role.
     *
     * @return `true` if the user is an AGENT or ADMIN, `false` otherwise.
     */
    fun isAgentOrAdmin(): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication.principal

        if (principal !is CustomUserDetails) {
            return false
        }
        return principal.user.role == UserRole.AGENT || principal.user.role == UserRole.ADMIN
    }
}