package com.asap.application.space.service

import com.asap.application.space.port.`in`.MainSpaceGetUsecase
import com.asap.application.space.port.`in`.SpaceGetUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SpaceQueryService(
    private val spaceManagementPort: SpaceManagementPort,
    private val userManagementPort: UserManagementPort
) : MainSpaceGetUsecase,
    SpaceGetUsecase {
    override fun get(query: MainSpaceGetUsecase.Query): MainSpaceGetUsecase.Response {
        val mainSpace =
            spaceManagementPort.getMainSpace(
                userId = DomainId(query.userId),
            )
        return MainSpaceGetUsecase.Response(
            id = mainSpace.id.value,
            username = userManagementPort.getUserNotNull(DomainId(query.userId)).username
        )
    }

    override fun getAll(query: SpaceGetUsecase.GetAllQuery): SpaceGetUsecase.GetAllResponse {
        val spaces =
            spaceManagementPort.getAllIndexedSpace(
                userId = DomainId(query.userId),
            )

        return SpaceGetUsecase.GetAllResponse(
            spaces =
            spaces.map {
                SpaceGetUsecase.SpaceDetail(
                    spaceName = it.name,
                    letterCount = 0,
                    isMainSpace = it.isMain(),
                    spaceIndex = it.index,
                    spaceId = it.id.value,
                )
            },
        )
    }
}
