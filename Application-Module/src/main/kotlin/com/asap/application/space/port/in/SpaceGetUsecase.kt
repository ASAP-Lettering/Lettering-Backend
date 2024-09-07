package com.asap.application.space.port.`in`

interface SpaceGetUsecase {

    fun getAll(query: GetAllQuery): GetAllResponse

    data class GetAllQuery(
        val userId: String
    )

    data class GetAllResponse(
        val spaces: List<SpaceDetail>
    )

    data class SpaceDetail(
        val spaceName: String,
        val letterCount: Int,
        val isMainSpace: Boolean,
        val spaceIndex: Int,
        val spaceId: String
    )
}