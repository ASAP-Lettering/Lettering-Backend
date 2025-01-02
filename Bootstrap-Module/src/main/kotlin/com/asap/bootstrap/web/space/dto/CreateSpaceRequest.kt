package com.asap.bootstrap.web.space.dto

data class CreateSpaceRequest(
    val spaceName: String,
    val templateType: Int,
) {
}