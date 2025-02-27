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
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime,
    var unregisterReason: String? = null,
) : Aggregate<User>(id, createdAt, updatedAt) {
    companion object {
        fun create(
            id: DomainId = DomainId.generate(),
            username: String,
            profileImage: String,
            email: String,
            permission: UserPermission,
            birthday: LocalDate?,
            createdAt: LocalDateTime = LocalDateTime.now(),
            updatedAt: LocalDateTime = LocalDateTime.now(),
        ): User =
            User(id, username, profileImage, email, permission, birthday, null, createdAt, updatedAt).also {
                it.registerEvent(UserEvent.UserCreatedEvent(it))
            }
    }

    fun delete(reason: String) {
        this.profileImage = "UNKNOWN"
        this.birthday = null
        this.unregisterReason = reason

        registerEvent(UserEvent.UserDeletedEvent(this))
        updateTime()
    }

    fun updateBirthday(birthday: LocalDate) {
        this.birthday = birthday
        updateTime()
    }

    fun updateOnboarding() {
        this.onboardingAt = LocalDateTime.now()
        updateTime()
    }

    fun isProcessedOnboarding(): Boolean {
        return this.onboardingAt != null
    }
}
