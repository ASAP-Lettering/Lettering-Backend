package com.asap.domain.space.entity

import com.asap.domain.common.DomainId

data class Space(
    val id: DomainId = DomainId.generate(),
    val userId: DomainId,
    val name: String,
    val templateType: Int
) {

    fun updateName(name: String): Space {
        return this.copy(name = name)
    }
}