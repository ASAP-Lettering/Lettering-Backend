package com.asap.client.oauth.platform.naver

data class NaverApiResponse(
    val resultcode: String,
    val message: String,
    val response: NaverUserResponse
)

data class NaverUserResponse(
    val id: String,
    val nickname: String,
    val name: String,
    val email: String,
    val gender: String,
    val age: String,
    val birthday: String,
    val profile_image: String,
    val birthyear: String,
    val mobile: String
)