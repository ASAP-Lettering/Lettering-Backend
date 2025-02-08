package com.asap.application.space.service

import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.space.port.`in`.GetMainSpaceUsecase
import com.asap.application.space.port.`in`.GetSpaceUsecase
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
) : GetMainSpaceUsecase,
    GetSpaceUsecase {
    override fun get(query: GetMainSpaceUsecase.Query): GetMainSpaceUsecase.Response {
        val mainSpace =
            spaceManagementPort.getMainSpace(
                userId = DomainId(query.userId),
            )
        val space =
            spaceManagementPort.getSpaceNotNull(
                userId = DomainId(query.userId),
                spaceId = mainSpace.id,
            )
        return GetMainSpaceUsecase.Response(
            id = mainSpace.id.value,
            username = userManagementPort.getUserNotNull(DomainId(query.userId)).username,
            templateType = space.templateType,
            spaceName = space.name,
        )
    }

    override fun getAll(query: GetSpaceUsecase.GetAllQuery): GetSpaceUsecase.GetAllResponse {
        val spaces =
            spaceManagementPort.getAllIndexedSpace(
                userId = DomainId(query.userId),
            )

        return GetSpaceUsecase.GetAllResponse(
            spaces =
                spaces.map {
                    GetSpaceUsecase.SpaceDetail(
                        spaceName = it.name,
                        letterCount = spaceLetterManagementPort.countSpaceLetterBy(it.id, DomainId(query.userId)),
                        isMainSpace = it.isMain,
                        spaceIndex = it.index,
                        spaceId = it.id.value,
                    )
                },
        )
    }

    override fun get(query: GetSpaceUsecase.GetQuery): GetSpaceUsecase.GetResponse {
        val space =
            spaceManagementPort.getSpaceNotNull(
                userId = DomainId(query.userId),
                spaceId = DomainId(query.spaceId),
            )
        return GetSpaceUsecase.GetResponse(
            spaceName = space.name,
            spaceId = space.id.value,
            templateType = space.templateType,
        )
    }
}
