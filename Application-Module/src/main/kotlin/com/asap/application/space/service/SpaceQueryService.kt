package com.asap.application.space.service

import com.asap.application.space.port.`in`.MainSpaceQueryUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service

@Service
class SpaceQueryService(
    private val spaceManagementPort: SpaceManagementPort,
) : MainSpaceQueryUsecase {
    override fun get(
        query: MainSpaceQueryUsecase.Query
    ): MainSpaceQueryUsecase.Response {
        val mainSpace = spaceManagementPort.getMainSpace(
            userId = DomainId(query.userId)
        )
        return MainSpaceQueryUsecase.Response(
            id = mainSpace.id.value
        )
    }
}