package com.asap.application.user.service

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.`in`.LogoutUsecase
import com.asap.application.user.port.`in`.ReissueTokenUsecase
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.application.user.vo.UserClaims
import com.asap.domain.UserFixture
import com.asap.domain.common.DomainId
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class UserTokenCommandServiceTest :
    BehaviorSpec({

        val mockUserTokenManagementPort = mockk<UserTokenManagementPort>(relaxed = true)
        val mockUserTokenConvertPort = mockk<UserTokenConvertPort>(relaxed = true)
        val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)

        val userTokenCommandService =
            UserTokenCommandService(
                mockUserTokenConvertPort,
                mockUserTokenManagementPort,
                mockUserManagementPort,
            )

        given("토큰 파싱 요청이 들어왔을 때") {
            val accessToken = "accessToken"
            val userClaims =
                UserClaims.Access(
                    userId = "userId",
                )
            every { mockUserTokenConvertPort.resolveAccessToken(accessToken) } returns userClaims
            `when`("토큰이 주어진다면") {
                val response = userTokenCommandService.resolveAccessToken(accessToken)
                then("토큰을 파싱하여 유저 아이디를 반환한다") {
                    response.userId shouldBe userClaims.userId
                }
            }
        }

        given("Refresh Token 재발급 요청이 들어왔을 때") {
            every { mockUserTokenManagementPort.isExistsToken("invalid") } returns false
            `when`("Refresh Token이 존재하지 않으면") {
                then("UserPermissionDeniedException을 반환한다.") {
                    shouldThrow<UserException.UserPermissionDeniedException> {
                        userTokenCommandService.reissue(ReissueTokenUsecase.Command("invalid"))
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
                val response = userTokenCommandService.reissue(ReissueTokenUsecase.Command(refreshToken))
                then("AccessToken과 RefreshToken을 반환한다.") {
                    response.accessToken.isNotEmpty() shouldBe true
                    response.refreshToken.isNotEmpty() shouldBe true
                }
            }
        }

        given("로그아웃 요청이 들어올 떄") {
            val command =
                LogoutUsecase.Command(
                    refreshToken = "logoutToken",
                    userId = "userId",
                )
            every {
                mockUserTokenManagementPort.isExistsToken(
                    token = command.refreshToken,
                    userId = DomainId(command.userId),
                )
            } returns true
            `when`("refreshToken이 존재하면") {
                userTokenCommandService.logout(command)
                then("로그아웃 처리를 한다.") {
                    verify {
                        mockUserTokenManagementPort.deleteUserToken(
                            token = command.refreshToken,
                        )
                    }
                }
            }

            `when`("refreshToken이 존재하지 않으면") {
                every {
                    mockUserTokenManagementPort.isExistsToken(
                        token = command.refreshToken,
                        userId = DomainId(command.userId),
                    )
                } returns false
                then("UserTokenNotFoundException을 반환한다.") {
                    shouldThrow<UserException.UserTokenNotFoundException> {
                        userTokenCommandService.logout(command)
                    }
                }
            }
        }
    })
