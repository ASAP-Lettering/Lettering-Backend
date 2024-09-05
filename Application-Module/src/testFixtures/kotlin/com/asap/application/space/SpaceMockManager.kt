package com.asap.application.space

import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId


class SpaceMockManager(
    private val spaceManagementPort: SpaceManagementPort
) {

    fun settingSpace(userId: String){
        spaceManagementPort.createSpace(DomainId(userId), "spaceName", 0)
    }
}