package com.helpdesk.api.controller

import com.helpdesk.api.config.CustomUserDetails
import com.helpdesk.api.config.UserSecurity
import com.helpdesk.api.dto.TicketCreateRequest
import com.helpdesk.api.dto.TicketResponse
import com.helpdesk.api.dto.TicketUpdateRequest
import com.helpdesk.api.model.UserRole
import com.helpdesk.api.service.TicketService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.util.UUID

@RestController
@RequestMapping("/api/tickets")
class TicketController(
    private val ticketService: TicketService,
    private val userSecurity: UserSecurity
) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('USER', 'AGENT', 'ADMIN')")
    fun createTicket(
        @Valid @RequestBody request: TicketCreateRequest,
        @AuthenticationPrincipal currentUser: CustomUserDetails
    ): TicketResponse {
        return ticketService.createTicket(request.subject, request.description, currentUser.id, request.priority)
    }

    @GetMapping
    fun getAllTickets(@AuthenticationPrincipal currentUser: CustomUserDetails): List<TicketResponse> { // Using Kotlin's List
        return when (currentUser.user.role) {
            UserRole.ADMIN -> ticketService.findAllTickets()
            UserRole.AGENT -> ticketService.findAllTickets()
            UserRole.USER -> ticketService.findTicketsByRequester(currentUser.id)
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT') or @userSecurity.canAccessTicket(#id, principal)")
    fun getTicketById(@PathVariable id: UUID): TicketResponse {
        return ticketService.findTicketById(id)
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT') or @userSecurity.canAccessTicket(#id, principal)")
    fun updateTicket(
        @PathVariable id: UUID,
        @Valid @RequestBody request: TicketUpdateRequest,
        @AuthenticationPrincipal currentUser: CustomUserDetails
    ): TicketResponse {
        val ticket = ticketService.findTicketEntityById(id)

        val isAgentOrAdmin = userSecurity.isAgentOrAdmin()
        val isRequester = ticket.requester.id == currentUser.id

        if (!isAgentOrAdmin && isRequester) {
            // Requester can only update subject and description of their own tickets
            if (request.status != null || request.priority != null || request.assignedAgentId != null) {
                throw org.springframework.security.access.AccessDeniedException("Requester can only update subject and description of their own tickets.")
            }
        }
        return ticketService.updateTicket(id, request.subject, request.description, request.status, request.priority, request.assignedAgentId)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAnyRole('ADMIN', 'AGENT')")
    fun deleteTicket(@PathVariable id: UUID) {
        ticketService.deleteTicket(id)
    }
}