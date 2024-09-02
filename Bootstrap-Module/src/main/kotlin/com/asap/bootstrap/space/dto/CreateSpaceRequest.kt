package com.asap.bootstrap.space.dto

data class CreateSpaceRequest(
    val spaceName: String,
    val templateType: Int,
) {
}