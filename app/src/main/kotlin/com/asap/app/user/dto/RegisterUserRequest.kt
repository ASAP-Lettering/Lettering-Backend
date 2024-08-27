package com.asap.app.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema(description = "회원 가입 요청")
data class RegisterUserRequest(
    @Schema(description = "register_token, 소셜 로그인이로부터 전달받은 토큰")
    val registerToken: String,
    @Schema(description = "서비스 이용약관 동의")
    val servicePermission: Boolean,
    @Schema(description = "개인정보 수집 및 이용 동의")
    val privatePermission: Boolean,
    @Schema(description = "마케팅 정보 수신 동의")
    val marketingPermission: Boolean,
    @Schema(description = "생년 월일, yyyy-MM-dd, 값이 안넘어올 수 있음")
    val birthday: LocalDate?
) {
}