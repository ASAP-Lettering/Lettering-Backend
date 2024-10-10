package com.asap.application.user.service

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.`in`.ReissueTokenUsecase
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.application.user.vo.UserClaims
import com.asap.domain.UserFixture
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class ReissueTokenServiceTest :
    BehaviorSpec({

        val mockUserTokenManagementPort = mockk<UserTokenManagementPort>(relaxed = true)
        val mockUserTokenConvertPort = mockk<UserTokenConvertPort>(relaxed = true)
        val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)

        val reissueTokenService =
            ReissueTokenService(
                mockUserTokenConvertPort,
                mockUserTokenManagementPort,
                mockUserManagementPort,
            )

        given("Refresh Token 재발급 요청이 들어왔을 때") {
            every { mockUserTokenManagementPort.isExistsToken("invalid") } returns false
            `when`("Refresh Token이 존재하지 않으면") {
                then("UserPermissionDeniedException을 반환한다.") {
                    shouldThrow<UserException.UserPermissionDeniedException> {
                        reissueTokenService.reissue(ReissueTokenUsecase.Command("invalid"))
                    }
                }
            }

            val refreshToken = "valid"
            every { mockUserTokenManagementPort.isExistsToken(refreshToken) } returns true
            every { mockUserTokenConvertPort.resolveRefreshToken(refreshToken) } returns
                UserClaims.Refresh(
                    userId = "123",
                )
            val mockUser = UserFixture.createUser("123")
            every { mockUserManagementPort.getUserNotNull(any()) } returns mockUser
            every { mockUserTokenConvertPort.generateAccessToken(mockUser) } returns "accessToken"
            every { mockUserTokenConvertPort.generateRefreshToken(mockUser) } returns "refreshToken"
            `when`("Refresh Token이 존재하면") {
                val response = reissueTokenService.reissue(ReissueTokenUsecase.Command(refreshToken))
                then("AccessToken과 RefreshToken을 반환한다.") {
                    response.accessToken.isNotEmpty() shouldBe true
                    response.refreshToken.isNotEmpty() shouldBe true
                }
            }
        }
    })
