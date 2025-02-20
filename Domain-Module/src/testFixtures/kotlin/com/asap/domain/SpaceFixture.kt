package com.asap.domain

import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space
import java.time.LocalDateTime

object SpaceFixture {
    fun createSpace(
        id: DomainId = DomainId.generate(),
        userId: DomainId = DomainId.generate(),
        name: String = "test",
        templateType: Int = 0,
        index : Int = 0,
        isMain: Boolean = false,
    ): Space {
        return Space(
            id = id,
            userId = userId,
            name = name,
            templateType = templateType,
            index = index,
            isMain = isMain,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now(),
        )
    }
}