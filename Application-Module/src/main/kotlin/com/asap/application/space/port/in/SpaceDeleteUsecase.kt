package com.asap.application.space.port.`in`

interface SpaceDeleteUsecase {


    fun deleteOne(
        command: DeleteOneCommand
    )

    fun deleteAll(
        command: DeleteAllCommand
    )

    data class DeleteOneCommand(
        val userId: String,
        val spaceId: String
    )

    data class DeleteAllCommand(
        val spaceIds: List<String>,
        val userId: String
    )
}