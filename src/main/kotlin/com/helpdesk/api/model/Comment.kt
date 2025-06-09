package com.helpdesk.api.model

import jakarta.persistence.*
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "comments")
data class Comment (
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    val id: UUID = UUID.randomUUID(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    var ticket: Ticket,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @Lob
    @Column(nullable = false)
    var content: String,

    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now()
)