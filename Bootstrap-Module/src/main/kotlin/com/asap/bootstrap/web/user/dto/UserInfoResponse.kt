package com.asap.bootstrap.web.user.dto

import java.time.LocalDate

data class UserInfoResponse(
    val name: String,
    val email: String,
    val socialPlatform: String,
    val birthday: LocalDate?,
)
