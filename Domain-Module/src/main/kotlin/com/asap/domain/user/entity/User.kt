package com.asap.domain.user.entity

import com.asap.domain.common.DomainId
import com.asap.domain.user.vo.UserPermission
import java.time.LocalDate

data class User(
    val id: DomainId = DomainId.generate(),
    var username: String,
    var profileImage: String,
    var email: String,
    val permission: UserPermission,
    var birthday: LocalDate?,
) {
    fun delete() {
        this.username = "UNKNOWN"
        this.profileImage = "UNKNOWN"
        this.email = "UNKNOWN"
        this.birthday = null
    }

    fun updateBirthday(birthday: LocalDate) {
        this.birthday = birthday
    }
}
