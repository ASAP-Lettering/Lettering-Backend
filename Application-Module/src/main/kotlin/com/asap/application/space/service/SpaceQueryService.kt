package com.asap.application.space.service

import com.asap.application.letter.port.out.SpaceLetterManagementPort
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
    private val userManagementPort: UserManagementPort,
    private val spaceLetterManagementPort: SpaceLetterManagementPort,
) : MainSpaceGetUsecase,
    SpaceGetUsecase {
    override fun get(query: MainSpaceGetUsecase.Query): MainSpaceGetUsecase.Response {
        val mainSpace =
            spaceManagementPort.getMainSpace(
                userId = DomainId(query.userId),
            )
        val space =
            spaceManagementPort.getSpaceNotNull(
                userId = DomainId(query.userId),
                spaceId = mainSpace.id,
            )
        return MainSpaceGetUsecase.Response(
            id = mainSpace.id.value,
            username = userManagementPort.getUserNotNull(DomainId(query.userId)).username,
            templateType = space.templateType,
            spaceName = space.name,
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
                        letterCount = spaceLetterManagementPort.countLetterBySpaceId(it.id),
                        isMainSpace = it.isMain(),
                        spaceIndex = it.index,
                        spaceId = it.id.value,
                    )
                },
        )
    }

    override fun get(query: SpaceGetUsecase.GetQuery): SpaceGetUsecase.GetResponse {
        val space =
            spaceManagementPort.getSpaceNotNull(
                userId = DomainId(query.userId),
                spaceId = DomainId(query.spaceId),
            )
        return SpaceGetUsecase.GetResponse(
            spaceName = space.name,
            spaceId = space.id.value,
            templateType = space.templateType,
        )
    }
}
