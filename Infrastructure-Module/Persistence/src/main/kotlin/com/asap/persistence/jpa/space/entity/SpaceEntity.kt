package com.asap.persistence.jpa.space.entity

import com.asap.domain.space.entity.Space
import com.asap.persistence.jpa.common.AggregateRoot
import com.asap.persistence.jpa.common.EntityStatus
import com.asap.persistence.jpa.user.entity.UserEntity
import jakarta.persistence.*

@Entity
@Table(name = "space")
class SpaceEntity(
    id: String,
    userId: String,
    name: String,
    templateType: Int,
    index: Int,
) : AggregateRoot<SpaceEntity>(id) {
    @Column(name = "user_id", nullable = false)
    var userId: String = userId

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, insertable = false, updatable = false)
    lateinit var user: UserEntity

    var name: String = name
    var templateType: Int = templateType

    @Column(
        name = "space_index",
        nullable = false,
    )
    var index: Int = index

    @Enumerated(EnumType.STRING)
    @Column(
        name = "space_status",
        nullable = false,
        columnDefinition = "varchar(20)",
    )
    var spaceStatus: EntityStatus = EntityStatus.ACTIVE


    @Column(
        name = "is_main",
        nullable = false,
    )
    var isMain: Boolean = false

    fun update(space: Space) {
        this.userId = space.userId.value
        this.name = space.name
        this.templateType = space.templateType
        update()
    }
}
