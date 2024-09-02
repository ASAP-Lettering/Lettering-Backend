package com.asap.domain.user.entity

import com.asap.domain.common.DomainId
import com.asap.domain.user.vo.UserPermission

data class User(
    val id: DomainId = DomainId.generate(),
    val nickname: String,
    val profileImage: String,
    val permission: UserPermission
) {
}