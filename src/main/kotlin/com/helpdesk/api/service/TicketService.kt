package com.helpdesk.api.service

import com.helpdesk.api.dto.TicketResponse
import com.helpdesk.api.exception.ResourceNotFoundException
import com.helpdesk.api.model.*
import com.helpdesk.api.repository.TicketRepository
import com.helpdesk.api.util.toResponse
import com.helpdesk.api.util.toResponse as mapTicketToResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

@Service
class TicketService(
    private val ticketRepository: TicketRepository,
    private val userService: UserService,
    private val commentService: CommentService
) {

    @Transactional
    fun createTicket(
        subject: String,
        description: String,
        requesterId: UUID,
        priority: TicketPriority = TicketPriority.MEDIUM
    ): TicketResponse {
        val requester = userService.findUserEntityById(requesterId)
        val newTicket = Ticket(
            subject = subject,
            description = description,
            requester = requester,
            priority = priority,
            status = TicketStatus.OPEN,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )
        val savedTicket = ticketRepository.save(newTicket)
        return savedTicket.mapTicketToResponse(emptyList())
    }

    @Transactional
    fun updateTicket(
        id: UUID,
        subject: String?,
        description: String?,
        status: TicketStatus?,
        priority: TicketPriority?,
        assignedAgentId: UUID?
    ): TicketResponse {
        val ticket = ticketRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Ticket not found with id: $id") }

        subject?.let { ticket.subject = it }
        description?.let { ticket.description = it }
        status?.let {
            if (it == TicketStatus.CLOSED && ticket.closedAt == null) {
                ticket.closedAt = LocalDateTime.now()
            } else if (it != TicketStatus.CLOSED && ticket.closedAt != null) {
                ticket.closedAt = null
            }
            ticket.status = it
        }
        priority?.let { ticket.priority = it }
        assignedAgentId?.let {
            val agent = userService.findUserEntityById(it)
            if (agent.role == UserRole.USER) {
                throw IllegalArgumentException("Cannot assign ticket to a regular user. Only AGENT or ADMIN roles are allowed.")
            }
            ticket.assignedAgent = agent
            if (ticket.status == TicketStatus.OPEN) {
                ticket.status = TicketStatus.ASSIGNED
            }
        } ?: run {
            if (ticket.assignedAgent != null) {
                ticket.assignedAgent = null
                if (ticket.status != TicketStatus.OPEN && ticket.status != TicketStatus.CLOSED && ticket.status != TicketStatus.RESOLVED) {
                    ticket.status = TicketStatus.OPEN
                }
            }
        }

        val savedTicket = ticketRepository.save(ticket)
        // Fetch and map comments after saving the ticket
        val comments = commentService.findCommentsByTicketEntity(savedTicket).map { it.toResponse() }
        return savedTicket.mapTicketToResponse(comments)
    }

    fun findTicketById(id: UUID): TicketResponse {
        val ticket = ticketRepository.findById(id).orElseThrow { ResourceNotFoundException("Ticket not found with id: $id") }
        val comments = commentService.findCommentsByTicketEntity(ticket).map { it.toResponse() }
        return ticket.mapTicketToResponse(comments)
    }

    fun findTicketEntityById(id: UUID): Ticket {
        return ticketRepository.findById(id).orElseThrow { ResourceNotFoundException("Ticket not found with id: $id") }
    }

    fun findAllTickets(): List<TicketResponse> { // Using Kotlin's List
        return ticketRepository.findAll().map { ticket ->
            val comments = commentService.findCommentsByTicketEntity(ticket).map { it.toResponse() }
            ticket.mapTicketToResponse(comments)
        }
    }

    fun findTicketsByRequester(requesterId: UUID): List<TicketResponse> {
        val requester = userService.findUserEntityById(requesterId)
        return ticketRepository.findByRequester(requester).map { ticket ->
            val comments = commentService.findCommentsByTicketEntity(ticket).map { it.toResponse() }
            ticket.mapTicketToResponse(comments)
        }
    }

    fun findTicketsByAssignedAgent(agentId: UUID): List<TicketResponse> {
        val agent = userService.findUserEntityById(agentId)
        return ticketRepository.findByAssignedAgent(agent).map { ticket ->
            val comments = commentService.findCommentsByTicketEntity(ticket).map { it.toResponse() }
            ticket.mapTicketToResponse(comments)
        }
    }

    @Transactional
    fun deleteTicket(id: UUID) {
        if (!ticketRepository.existsById(id)) {
            throw ResourceNotFoundException("Ticket not found with id: $id")
        }
        ticketRepository.deleteById(id)
    }
}