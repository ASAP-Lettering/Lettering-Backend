package com.asap.client.oauth.platform

import com.asap.client.ClientProperties
import com.asap.client.NaverOAuthProperties
import com.asap.client.OAuthProperties
import com.asap.client.oauth.OAuthRetrieveHandler
import com.asap.client.oauth.exception.OAuthException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient

class NaverOAuthRetrieveHandlerTest :
    BehaviorSpec({
        var mockWebServer =
            MockWebServer().also {
                it.start()
            }
        var naverWebClient: WebClient =
            WebClient
                .builder()
                .baseUrl(mockWebServer.url("/").toString())
                .defaultHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                .build()

        val config =
            ClientProperties(
                oauth =
                    OAuthProperties(
                        naver =
                            NaverOAuthProperties(
                                clientId = "test-client-id",
                                clientSecret = "test-client-secret",
                            ),
                    ),
            )
        var naverOAuthRetrieveHandler = NaverOAuthRetrieveHandler(naverWebClient, naverWebClient, config)

        given("OAuth 요청이 성공적으로 처리되었을 때") {
            val accessToken = "test-access-token"
            val request = OAuthRetrieveHandler.OAuthRequest(accessToken)

            val responseBody =
                """
                {
                    "resultcode": "00",
                    "message": "success",
                    "response": {
                        "id": "12345",
                        "nickname": "Test User",
                        "name": "Test Name",
                        "email": "test@example.com",
                        "gender": "M",
                        "age": "20-29",
                        "birthday": "01-01",
                        "profile_image": "https://example.com/profile.jpg",
                        "birthyear": "1990",
                        "mobile": "010-1234-5678"
                    }
                }
                """.trimIndent()

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody(responseBody),
            )

            `when`("getOAuthInfo 메소드를 호출하면") {
                val response = naverOAuthRetrieveHandler.getOAuthInfo(request)

                then("올바른 OAuthResponse를 반환해야 한다") {
                    response.socialId shouldBe "12345"
                    response.email shouldBe "test@example.com"

                    // 요청 검증
                    val recordedRequest = mockWebServer.takeRequest()
                    recordedRequest.path shouldBe "/v1/nid/me"
                    recordedRequest.getHeader("Authorization") shouldBe "Bearer test-access-token"
                }
            }
        }

        given("API가 오류를 반환할 때") {
            val accessToken = "test-access-token"
            val request = OAuthRetrieveHandler.OAuthRequest(accessToken)

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(401)
                    .setHeader("Content-Type", "application/json")
                    .setBody("{\"error\": \"invalid_token\"}"),
            )

            `when`("getOAuthInfo 메소드를 호출하면") {
                then("OAuthRetrieveFailedException이 발생해야 한다") {
                    shouldThrow<OAuthException.OAuthRetrieveFailedException> {
                        naverOAuthRetrieveHandler.getOAuthInfo(request)
                    }
                }
            }
        }

        given("응답이 null일 때") {
            val accessToken = "test-access-token"
            val request = OAuthRetrieveHandler.OAuthRequest(accessToken)

            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(200)
                    .setHeader("Content-Type", "application/json")
                    .setBody("null"),
            )

            `when`("getOAuthInfo 메소드를 호출하면") {
                then("OAuthRetrieveFailedException이 발생해야 한다") {
                    shouldThrow<OAuthException.OAuthRetrieveFailedException> {
                        naverOAuthRetrieveHandler.getOAuthInfo(request)
                    }
                }
            }
        }
    })
