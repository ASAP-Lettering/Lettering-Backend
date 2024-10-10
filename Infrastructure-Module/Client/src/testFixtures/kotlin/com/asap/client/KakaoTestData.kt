package com.asap.client

object KakaoTestData {
    val KAKAO_OAUTH_SUCCESS_RESPONSE =
        MockServer.Response(
            responseCode = 200,
            body =
                """
{
    "id":"socialId",
    "connected_at": "2022-04-11T01:45:28Z",
    "kakao_account": { 
        "email": "sample@sample.com"
    },
    "properties": {
        "nickname": "nickname",
        "profile_image": "test"
    }
}
                """.trimIndent(),
            headers =
                mapOf(
                    "Content-Type" to "application/json",
                ),
        )

    val KAKAO_OAUTH_FAIL_RESPONSE_WITH_NON_REGISTERED =
        MockServer.Response(
            responseCode = 200,
            body =
                """
{
    "id":"non-registered",
    "connected_at": "2022-04-11T01:45:28Z",
    "kakao_account": { 
        "email": "sample@sample.com"
    },
    "properties": {
        "nickname": "nickname",
        "profile_image": "test"
    }
}
                """.trimIndent(),
            headers =
                mapOf(
                    "Content-Type" to "application/json",
                ),
        )

    val KAKAO_OAUTH_FAIL_RESPONSE_WITH_INVALID_ACCESS_TOKEN =
        MockServer.Response(
            responseCode = 401,
            body =
                """
                {
                    "error": "invalid_token",
                    "error_description": "An error occurred while attempting to retrieve the access token."
                }
                """.trimIndent(),
            headers =
                mapOf(
                    "Content-Type" to "application/json",
                ),
        )
}
