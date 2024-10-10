package com.asap.application.space.port.`in`

interface SpaceGetUsecase {
    fun getAll(query: GetAllQuery): GetAllResponse

    fun get(query: GetQuery): GetResponse

    data class GetAllQuery(
        val userId: String,
    )

    data class GetQuery(
        val userId: String,
        val spaceId: String,
    )

    data class GetAllResponse(
        val spaces: List<SpaceDetail>,
    )

    data class SpaceDetail(
        val spaceName: String,
        val letterCount: Long,
        val isMainSpace: Boolean,
        val spaceIndex: Int,
        val spaceId: String,
    )

    data class GetResponse(
        val spaceName: String,
        val spaceId: String,
        val templateType: Int,
    )
}
