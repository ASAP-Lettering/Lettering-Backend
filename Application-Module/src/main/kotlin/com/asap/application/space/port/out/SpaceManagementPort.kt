package com.asap.application.space.port.out

import com.asap.domain.space.entity.MainSpace

interface SpaceManagementPort {

    fun getMainSpace(
        userId: String
    ): MainSpace
}