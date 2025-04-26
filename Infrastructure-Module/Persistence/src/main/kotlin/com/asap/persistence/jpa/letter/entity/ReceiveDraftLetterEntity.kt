package com.asap.persistence.jpa.letter.entity

import com.asap.domain.letter.entity.ReceiveDraftLetterType
import com.asap.persistence.jpa.common.BaseEntity
import com.asap.persistence.jpa.user.entity.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(
    name = "receive_draft_letters",
)
class ReceiveDraftLetterEntity(
    id: String,
    content: String,
    senderName: String,
    ownerId: String,
    images: List<String>,
    type: ReceiveDraftLetterType,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
) : BaseEntity(id, createdAt, updatedAt) {
    @Column(
        name = "content",
        nullable = false,
        columnDefinition = "text",
    )
    var content: String = content
    var senderName: String = senderName

    @Column(
        name = "owner_id",
        nullable = false,
    )
    var ownerId: String = ownerId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "owner_id",
        insertable = false,
        updatable = false,
    )
    lateinit var owner: UserEntity

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(
        name = "images",
        nullable = false,
        columnDefinition = "text",
    )
    var images: List<String> = images

    @Enumerated(EnumType.STRING)
    @Column(
        name = "type",
        nullable = false,
        columnDefinition = "VARCHAR(20)",
    )
    var type: ReceiveDraftLetterType = type
}
