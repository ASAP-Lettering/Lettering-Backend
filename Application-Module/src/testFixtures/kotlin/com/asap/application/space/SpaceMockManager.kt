package com.asap.application.space

import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space

class SpaceMockManager(
    private val spaceManagementPort: SpaceManagementPort,
) {
    fun settingSpace(
        userId: String,
        index: Int = 0,
    ): Space {
        val space =
            Space.create(
                userId = DomainId(userId),
                name = "test",
                templateType = 0,
            )
        return spaceManagementPort.save(space).also {
            spaceManagementPort.getIndexedSpaceNotNull(DomainId(userId), it.id).apply {
                updateIndex(index)
                spaceManagementPort.update(this)
            }
        }
    }

    fun getSpaceIndexes(userId: String): List<Pair<String, Int>> =
        spaceManagementPort.getAllIndexedSpace(DomainId(userId)).map {
            it.id.value to it.index
        }
}
