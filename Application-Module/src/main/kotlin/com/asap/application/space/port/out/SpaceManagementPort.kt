package com.asap.application.space.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.space.entity.Space

interface SpaceManagementPort {

    fun getMainSpace(
        userId: DomainId
    ): MainSpace

    fun createSpace(
        userId: DomainId,
        spaceName: String,
        templateType: Int
    ): Space

}