package com.asap.application.space.port.out

import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.IndexedSpace
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.space.entity.Space

interface SpaceManagementPort {

    fun getMainSpace(
        userId: DomainId
    ): MainSpace

    fun getSpace(
        userId: DomainId,
        spaceId: DomainId
    ): Space

    fun getAllIndexedSpace(
        userId: DomainId,
    ): List<IndexedSpace>

    fun createSpace(
        userId: DomainId,
        spaceName: String,
        templateType: Int
    ): Space

    fun update(space: Space): Space

}