package com.asap.bootstrap.web.space.dto

data class GetAllSpaceResponse(
    val spaces: List<SpaceDetail>,
) {
    data class SpaceDetail(
        val spaceName: String,
        val letterCount: Long,
        val isMainSpace: Boolean,
        val spaceIndex: Int,
        val spaceId: String,
    )
}
