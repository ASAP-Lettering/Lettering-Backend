package com.asap.persistence.jpa.space.entity

import com.asap.domain.space.entity.Space
import com.asap.persistence.jpa.common.AggregateRoot
import com.asap.persistence.jpa.common.EntityStatus
import com.asap.persistence.jpa.user.entity.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "space")
class SpaceEntity(
    userId: String,
    name: String,
    templateType: Int,
    index: Int,
) : AggregateRoot<SpaceEntity>() {
    @Column(name = "user_id", nullable = false)
    var userId: String = userId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    lateinit var user: UserEntity

    var name: String = name
    var templateType: Int = templateType
    var index: Int = index

    @Enumerated(EnumType.STRING)
    @Column(
        name = "space_status",
        nullable = false,
        columnDefinition = "varchar(20)",
    )
    var spaceStatus: EntityStatus = EntityStatus.ACTIVE

    companion object {
        fun default(
            userId: String,
            name: String,
            templateType: Int,
            index: Int,
        ) = SpaceEntity(
            userId = userId,
            name = name,
            templateType = templateType,
            index = index,
        )
    }

    fun update(space: Space) {
        this.userId = space.userId.value
        this.name = space.name
        this.templateType = space.templateType
    }
}
