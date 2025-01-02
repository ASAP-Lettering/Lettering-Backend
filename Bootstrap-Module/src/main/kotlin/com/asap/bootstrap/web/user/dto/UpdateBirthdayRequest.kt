package com.asap.bootstrap.web.user.dto

import java.time.LocalDate

data class UpdateBirthdayRequest(
    val birthday: LocalDate,
)
