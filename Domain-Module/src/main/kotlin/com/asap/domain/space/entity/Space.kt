package com.asap.domain.space.entity

import com.asap.domain.common.DomainId

data class Space(
    val id: DomainId = DomainId.generate(),
    val userId: DomainId,
    val name: String,
    val templateType: Int,
) {
    companion object {
        fun create(
            userId: DomainId,
            name: String,
            templateType: Int,
        ): Space =
            Space(
                userId = userId,
                name = name,
                templateType = templateType,
            )
    }

    fun updateName(name: String): Space = this.copy(name = name)
}
