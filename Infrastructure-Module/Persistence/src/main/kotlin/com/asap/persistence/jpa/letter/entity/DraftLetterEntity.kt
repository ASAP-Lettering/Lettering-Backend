package com.asap.persistence.jpa.letter.entity

import com.asap.persistence.jpa.common.AggregateRoot
import com.asap.persistence.jpa.user.entity.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(
    name = "draft_letters",
)
class DraftLetterEntity(
    id: String,
    content: String,
    receiverName: String,
    ownerId: String,
    images: List<String>,
    updatedAt: LocalDateTime,
) : AggregateRoot<DraftLetterEntity>(id) {
    var content: String = content
    var receiverName: String = receiverName

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

    override var updatedAt: LocalDateTime = updatedAt
}
