package com.asap.application.space.service

import com.asap.application.space.port.`in`.MainSpaceQueryUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.utils.UserUtils
import org.springframework.stereotype.Service

@Service
class MainSpaceQueryService(
    private val spaceManagementPort: SpaceManagementPort,
    private val userUtils: UserUtils
): MainSpaceQueryUsecase {
    override fun query(): MainSpaceQueryUsecase.Response {
        val mainSpace = spaceManagementPort.getMainSpace(
            userId = userUtils.getAccessUserId()
        )
        return MainSpaceQueryUsecase.Response(
            id = mainSpace.id
        )
    }
}