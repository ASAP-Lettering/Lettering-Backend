package com.asap.application.space.port.`in`

interface DeleteSpaceUsecase {
    fun deleteOne(command: DeleteOneCommand)

    fun deleteAllBy(command: DeleteAllCommand)

    fun deleteAllBy(command: DeleteAllUser)

    data class DeleteOneCommand(
        val userId: String,
        val spaceId: String,
    )

    data class DeleteAllCommand(
        val spaceIds: List<String>,
        val userId: String,
    )

    data class DeleteAllUser(
        val userId: String,
    )
}
