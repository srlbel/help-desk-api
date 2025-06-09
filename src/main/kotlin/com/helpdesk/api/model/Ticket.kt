package com.helpdesk.api.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "tickets")
data class Ticket (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var subject: String,

    @Lob
    @Column(nullable = false)
    var description: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TicketStatus = TicketStatus.OPEN,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priority: TicketPriority = TicketPriority.MEDIUM,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    var requester: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    var assignedAgent: User? = null,

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    var closedAt: LocalDateTime? = null,

    @OneToMany(mappedBy = "ticket", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var comments: MutableList<Comment> = mutableListOf()
    )