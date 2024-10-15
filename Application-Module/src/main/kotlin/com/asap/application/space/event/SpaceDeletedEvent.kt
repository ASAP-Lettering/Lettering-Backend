package com.asap.application.space.event

data class SpaceDeletedEvent(
    val userId: String,
    val spaceId: String,
)
