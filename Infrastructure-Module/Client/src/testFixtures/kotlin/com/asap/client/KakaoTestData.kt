package com.asap.client

import okhttp3.mockwebserver.MockResponse

object KakaoTestData {

    val KAKAO_OAUTH_SUCCESS_RESPONSE: MockResponse = MockResponse()
        .setResponseCode(200)
        .addHeader("Content-Type", "application/json")
        .setBody(
            """
            {
                "id": "socialId",
                "properties": {
                    "nickname": "nickname",
                    "profile_image": "test"
                }
            }
            """.trimIndent()
        )

    val KAKAO_OAUTH_FAIL_RESPONSE_WITH_NON_REGISTERED: MockResponse = MockResponse()
        .setResponseCode(200)
        .addHeader("Content-Type", "application/json")
        .setBody(
            """
            {
                "id": "non-registered",
                "properties": {
                    "nickname": "nickname",
                    "profile_image": "test"
                }
            }
            """.trimIndent()
        )

    val KAKAO_OAUTH_FAIL_RESPONSE_WITH_INVALID_ACCESS_TOKEN: MockResponse = MockResponse()
        .setResponseCode(400)
        .addHeader("Content-Type", "application/json")
        .setBody(
            """
            {
                "error": "invalid_token",
                "error_description": "An error occurred while attempting to retrieve the access token."
            }
            """.trimIndent()
        )

}