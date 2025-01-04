package com.asap.bootstrap.web.space.dto

data class MainSpaceInfoResponse(
    val spaceId: String,
    val username: String,
    val templateType: Int,
    val spaceName: String,
)
