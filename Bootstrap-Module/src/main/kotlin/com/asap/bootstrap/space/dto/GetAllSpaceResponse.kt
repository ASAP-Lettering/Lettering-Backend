package com.asap.bootstrap.space.dto

data class GetAllSpaceResponse(
    val spaces: List<SpaceDetail>
) {


    data class SpaceDetail(
        val spaceName: String,
        val letterCount: Int,
        val isMainSpace: Boolean,
        val spaceIndex: Int,
        val spaceId: String
    )
}