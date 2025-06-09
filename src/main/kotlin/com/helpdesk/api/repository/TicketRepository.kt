package com.helpdesk.api.repository

import com.helpdesk.api.model.Ticket
import com.helpdesk.api.model.TicketStatus
import com.helpdesk.api.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface TicketRepository : JpaRepository<Ticket, UUID> {
    fun findByRequester(requester: User): List<Ticket>
    fun findByAssignedAgent(assignedAgent: User): List<Ticket>
    fun findByStatus(status: TicketStatus): List<Ticket>
}