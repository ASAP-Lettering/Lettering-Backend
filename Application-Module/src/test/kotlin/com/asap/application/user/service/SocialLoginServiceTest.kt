package com.asap.application.user.service

import com.asap.application.user.port.`in`.SocialLoginUsecase
import com.asap.application.user.port.out.*
import com.asap.application.user.vo.AuthInfo
import com.asap.common.exception.DefaultException
import com.asap.domain.common.DomainId
import com.asap.domain.user.entity.User
import com.asap.domain.user.entity.UserAuth
import com.asap.domain.user.enums.SocialLoginProvider
import com.asap.domain.user.vo.UserPermission
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class SocialLoginServiceTest : BehaviorSpec({

    val mockUserAuthManagementPort = mockk<UserAuthManagementPort>()
    val mockAuthInfoRetrievePort = mockk<AuthInfoRetrievePort>()
    val mockUserManagementPort = mockk<UserManagementPort>()
    val mockUserTokenConvertPort = mockk<UserTokenConvertPort>()
    val mockUserTokenManagementPort = mockk<UserTokenManagementPort>(relaxed = true)

    val socialLoginService = SocialLoginService(
        mockUserAuthManagementPort,
        mockAuthInfoRetrievePort,
        mockUserTokenConvertPort,
        mockUserManagementPort,
        mockUserTokenManagementPort
    )

    given("소셜 로그인에서 요청한 사용자가") {
        var command = SocialLoginUsecase.Command(SocialLoginProvider.KAKAO.name, "registered")
        val authInfo = AuthInfo(SocialLoginProvider.KAKAO, "socialId", "name", "profileImage")
        val getUserAuth = UserAuth(
            userId = DomainId.generate(),
            socialId = "socialId",
            socialLoginProvider = SocialLoginProvider.KAKAO
        )
        val getUser = User(
            id = getUserAuth.userId,
            username = authInfo.username,
            permission = UserPermission(true, true, true),
            profileImage = authInfo.profileImage
        )
        every { mockAuthInfoRetrievePort.getAuthInfo(SocialLoginProvider.KAKAO, "registered") } returns authInfo
        every {
            mockUserAuthManagementPort.getUserAuth(
                authInfo.socialId,
                authInfo.socialLoginProvider
            )
        } returns getUserAuth
        every{ mockUserManagementPort.getUser(any()) } returns getUser
        every { mockUserTokenConvertPort.generateAccessToken(getUser) } returns "accessToken"
        every { mockUserTokenConvertPort.generateRefreshToken(getUser) } returns "refreshToken"
        `when`("기존에 존재한다면") {
            val response = socialLoginService.login(command)
            then("access token과 refresh token을 반환하는 success 인스턴스를 반환한다.") {
                response.shouldBeInstanceOf<SocialLoginUsecase.Success>()
                response.accessToken.isNotEmpty() shouldBe true
                response.refreshToken.isNotEmpty() shouldBe true
                verify { mockUserTokenManagementPort.saveUserToken(any()) }
            }
        }

        every { mockUserManagementPort.getUser(any()) } returns null
        `when`("인증 정보만 존재하면 존재하고 사용자 정보가 없다면") {

            then("InvalidStateException 예외가 발생한다.") {
                shouldThrow<DefaultException.InvalidStateException> {
                    socialLoginService.login(command)
                }
            }
        }

        command = SocialLoginUsecase.Command(SocialLoginProvider.KAKAO.name, "nonRegistered")
        every { mockAuthInfoRetrievePort.getAuthInfo(SocialLoginProvider.KAKAO, "nonRegistered") } returns authInfo
        every { mockUserAuthManagementPort.getUserAuth(authInfo.socialId, authInfo.socialLoginProvider) } returns null
        every { mockUserTokenConvertPort.generateRegisterToken(authInfo.socialId, authInfo.socialLoginProvider.name, authInfo.username, authInfo.profileImage) } returns "registerToken"
        `when`("가입되지 않았다면") {
            val response = socialLoginService.login(command)
            then("register token을 반환하는 nonRegistered 인스턴스를 반환한다.") {
                response.shouldBeInstanceOf<SocialLoginUsecase.NonRegistered>()
                response.registerToken.isNotEmpty() shouldBe true
                verify { mockUserTokenManagementPort.saveUserToken(any()) }
            }
        }
    }
})