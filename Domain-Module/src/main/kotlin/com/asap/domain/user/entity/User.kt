package com.asap.domain.user.entity

import com.asap.application.user.event.UserEvent
import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId
import com.asap.domain.user.vo.UserPermission
import java.time.LocalDate
import java.time.LocalDateTime

class User(
    id: DomainId,
    var username: String,
    var profileImage: String,
    var email: String,
    val permission: UserPermission,
    var birthday: LocalDate?,
    var onboardingAt: LocalDateTime?,
) : Aggregate<User>(id) {
    companion object {
        fun create(
            id: DomainId = DomainId.generate(),
            username: String,
            profileImage: String,
            email: String,
            permission: UserPermission,
            birthday: LocalDate?,
        ): User =
            User(id, username, profileImage, email, permission, birthday, null).also {
                it.registerEvent(UserEvent.UserCreatedEvent(it))
            }
    }

    fun delete() {
        this.username = "UNKNOWN"
        this.profileImage = "UNKNOWN"
        this.email = "UNKNOWN"
        this.birthday = null

        registerEvent(UserEvent.UserDeletedEvent(this))
    }

    fun updateBirthday(birthday: LocalDate) {
        this.birthday = birthday
    }

    fun updateOnboarding() {
        this.onboardingAt = LocalDateTime.now()
    }

    fun isProcessedOnboarding(): Boolean {
        return this.onboardingAt != null
    }
}
