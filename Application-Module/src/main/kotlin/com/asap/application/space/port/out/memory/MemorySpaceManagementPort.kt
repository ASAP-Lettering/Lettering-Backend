package com.asap.application.space.port.out.memory

import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.space.entity.MainSpace
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MemorySpaceManagementPort(

): SpaceManagementPort {



    override fun getMainSpace(
        userId: String
    ): MainSpace {
        return MainSpace(
            "mainSpaceId",
        )
    }

}