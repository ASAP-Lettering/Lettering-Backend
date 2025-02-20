package com.asap.persistence.jpa.letter.entity

import com.asap.domain.letter.entity.LetterLogType
import com.asap.persistence.jpa.common.BaseEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "letter_logs"
)
class LetterLogEntity(
    id: String,
    targetLetterId: String,
    loggedAt: LocalDateTime,
    logType: LetterLogType,
    content: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
):BaseEntity(id, createdAt, updatedAt) {
    @Column(
        name = "target_letter_id",
        nullable = false,
    )
    var targetLetterId: String = targetLetterId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "target_letter_id",
        insertable = false,
        updatable = false,
    )
    lateinit var targetLetter: SendLetterEntity


    var loggedAt: LocalDateTime = loggedAt

    @Enumerated(EnumType.STRING)
    @Column(
        name = "log_type",
        nullable = false,
        columnDefinition = "VARCHAR(20)",
    )
    var logType: LetterLogType = logType

    @Column(
        name = "content",
        nullable = false,
        columnDefinition = "varchar(500)",
    )
    var content: String = content
}