package com.asap.persistence.jpa.letter.entity

import com.asap.persistence.jpa.common.AggregateRoot
import com.asap.persistence.jpa.common.EntityStatus
import com.asap.persistence.jpa.space.entity.SpaceEntity
import com.asap.persistence.jpa.user.entity.UserEntity
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "receive_letter")
class ReceiveLetterEntity(
    id: String,
    content: String,
    images: List<String>,
    templateType: Int,
    senderId: String? = null,
    senderName: String,
    receiverId: String,
    receiveDate: LocalDate,
    movedAt: LocalDateTime,
    isOpened: Boolean,
    spaceId: String? = null,
) : AggregateRoot<ReceiveLetterEntity>(id) {
    @Column(
        name = "content",
        nullable = false,
        columnDefinition = "TEXT",
    )
    var content: String = content

    @Column(
        name = "images",
        nullable = false,
        columnDefinition = "json",
    )
    @JdbcTypeCode(SqlTypes.JSON)
    var images: List<String> = images

    var templateType: Int = templateType

    @Column(
        name = "sender_id",
        nullable = true,
    )
    var senderId: String? = senderId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    var sender: UserEntity? = null
    var senderName: String = senderName

    @Column(
        name = "receiver_id",
        nullable = false,
    )
    var receiverId: String = receiverId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    lateinit var receiver: UserEntity

    var receiveDate: LocalDate = receiveDate

    var movedAt: LocalDateTime = movedAt

    var isOpened: Boolean = isOpened

    @Column(
        name = "space_id",
        nullable = true,
    )
    var spaceId: String? = spaceId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "space_id", insertable = false, updatable = false)
    var space: SpaceEntity? = null

    var entityStatus: EntityStatus = EntityStatus.ACTIVE
}
