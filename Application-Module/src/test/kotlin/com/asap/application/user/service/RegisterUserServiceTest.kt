package com.asap.application.user.service

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.`in`.RegisterUserUsecase
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.application.user.vo.UserClaims
import com.asap.common.exception.DefaultException
import com.asap.domain.user.enums.SocialLoginProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate


class RegisterUserServiceTest: BehaviorSpec({

    val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)
    val mockUserAuthManagementPort = mockk<UserAuthManagementPort>(relaxed=true)
    val mockUserTokenManagementPort = mockk<UserTokenManagementPort>()


    val registerUserService = RegisterUserService(mockUserTokenManagementPort, mockUserAuthManagementPort, mockUserManagementPort)


    given("회원 가입 요청이 들어왔을 때") {
        val successCommand = RegisterUserUsecase.Command("valid", true, true, true, LocalDate.now())
        every { mockUserTokenManagementPort.resolveRegisterToken("valid") } returns UserClaims.Register(
            socialId = "123",
            socialLoginProvider = SocialLoginProvider.KAKAO,
            username = "test"
        )
        every { mockUserAuthManagementPort.isExistsUserAuth("123", SocialLoginProvider.KAKAO) } returns false
        every { mockUserTokenManagementPort.generateAccessToken(any()) } returns "accessToken"
        every { mockUserTokenManagementPort.generateRefreshToken(any()) } returns "refreshToken"
        `when`("회원 가입이 성공하면") {
            val response = registerUserService.registerUser(successCommand)
            then("access token과 refresh token을 반환한다.") {
                response.accessToken.isNotEmpty() shouldBe true
                response.refreshToken.isNotEmpty() shouldBe true
                verify { mockUserManagementPort.saveUser(any()) }
            }
        }


        every { mockUserTokenManagementPort.resolveRegisterToken("duplicate") } returns UserClaims.Register(
            socialId = "duplicate",
            socialLoginProvider = SocialLoginProvider.KAKAO,
            username = "test"
        )
        every { mockUserAuthManagementPort.isExistsUserAuth("duplicate", SocialLoginProvider.KAKAO) } returns true
        `when`("중복 가입 요청이 들어왔을 때") {
            val failCommand = RegisterUserUsecase.Command("duplicate", true, true, true, LocalDate.now())
            then("UserAlreadyRegisteredException 예외가 발생한다.") {
                shouldThrow<UserException.UserAlreadyRegisteredException> {
                    registerUserService.registerUser(failCommand)
                }
            }
        }

        every { mockUserTokenManagementPort.resolveRegisterToken("invalid") } throws IllegalArgumentException("Invalid token")
        `when`("register token이 유요하지 않다면") {
            val failCommandWithoutRegisterToken =
                RegisterUserUsecase.Command("invalid", true, true, true, LocalDate.now())
            then("예외가 발생한다.") {
                shouldThrow<Exception> {
                    registerUserService.registerUser(failCommandWithoutRegisterToken)
                }
            }
        }


        every { mockUserTokenManagementPort.resolveRegisterToken("valid") } returns UserClaims.Register(
            socialId = "123",
            socialLoginProvider = SocialLoginProvider.KAKAO,
            username = "test"
        )
        every{ mockUserAuthManagementPort.isExistsUserAuth("123", SocialLoginProvider.KAKAO) } returns false
        `when`("서비스 동의를 하지 않았다면") {
            val failCommandWithoutServicePermission = RegisterUserUsecase.Command("valid", false, true, true, LocalDate.now())
            then("InvalidPropertyException 예외가 발생한다.") {
                shouldThrow<DefaultException.InvalidDefaultException> {
                    registerUserService.registerUser(failCommandWithoutServicePermission)
                }
            }
        }

        `when`("개인정보 동의를 하지 않았다면") {
            val failCommandWithoutPrivatePermission = RegisterUserUsecase.Command("valid", true, false, true, LocalDate.now())
            then("InvalidPropertyException 예외가 발생한다.") {
                shouldThrow<DefaultException.InvalidDefaultException> {
                    registerUserService.registerUser(failCommandWithoutPrivatePermission)
                }
            }
        }
    }

}) {
}