package com.asap.persistence.jpa.letter.entity

import com.asap.domain.letter.enums.LetterStatus
import com.asap.persistence.jpa.common.AggregateRoot
import com.asap.persistence.jpa.common.EntityStatus
import com.asap.persistence.jpa.user.entity.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(
    name = "send_letter",
    indexes = [
        Index(
            name = "idx_letter_code",
            columnList = "letter_code",
            unique = true,
        ),
    ],
)
class SendLetterEntity(
    id: String,
    receiverName: String,
    content: String,
    images: List<String>,
    templateType: Int,
    senderId: String,
    letterCode: String?,
    receiverId: String?,
    letterStatus: LetterStatus,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : AggregateRoot<SendLetterEntity>(id, createdAt, updatedAt) {
    @Column(
        name = "content",
        nullable = false,
        columnDefinition = "text",
    )
    var content: String = content

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
        name = "images",
        columnDefinition = "json",
        nullable = false,
    )
    var images: List<String> = images
    var templateType: Int = templateType

    @Column(name = "sender_id", nullable = false)
    var senderId: String = senderId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "sender_id",
        nullable = false,
        insertable = false,
        updatable = false,
    )
    lateinit var sender: UserEntity

    @Column(name = "receiver_id")
    var receiverId: String? = receiverId

    var receiverName: String = receiverName

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "receiver_id",
        insertable = false,
        updatable = false,
    )
    var receiver: UserEntity? = null

    var letterCode: String? = letterCode

    @Enumerated(EnumType.STRING)
    @Column(
        name = "letter_status",
        nullable = false,
        columnDefinition = "varchar(20)",
    )
    var letterStatus: LetterStatus = letterStatus

    @Enumerated(EnumType.STRING)
    @Column(
        name = "entity_status",
        nullable = false,
        columnDefinition = "varchar(20)",
    )
    var entityStatus: EntityStatus = EntityStatus.ACTIVE
}
