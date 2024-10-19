package com.asap.bootstrap.user.dto

import java.time.LocalDate

data class UpdateBirthdayRequest(
    val birthday: LocalDate,
)
