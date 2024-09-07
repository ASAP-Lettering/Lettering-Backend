package com.asap.application.space.service

import com.asap.application.space.port.`in`.MainSpaceGetUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service

@Service
class SpaceQueryService(
    private val spaceManagementPort: SpaceManagementPort,
) : MainSpaceGetUsecase {
    override fun get(
        query: MainSpaceGetUsecase.Query
    ): MainSpaceGetUsecase.Response {
        val mainSpace = spaceManagementPort.getMainSpace(
            userId = DomainId(query.userId)
        )
        return MainSpaceGetUsecase.Response(
            id = mainSpace.id.value
        )
    }
}