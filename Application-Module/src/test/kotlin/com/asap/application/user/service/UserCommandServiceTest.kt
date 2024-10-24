package com.asap.application.user.service

import com.asap.application.user.exception.UserException
import com.asap.application.user.port.`in`.RegisterUserUsecase
import com.asap.application.user.port.`in`.UpdateUserUsecase
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.port.out.UserTokenManagementPort
import com.asap.application.user.vo.UserClaims
import com.asap.common.exception.DefaultException
import com.asap.domain.UserFixture
import com.asap.domain.user.enums.SocialLoginProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDate

class UserCommandServiceTest :
    BehaviorSpec({

        val mockUserManagementPort = mockk<UserManagementPort>(relaxed = true)
        val mockUserAuthManagementPort = mockk<UserAuthManagementPort>(relaxed = true)
        val mockUserTokenConvertPort = mockk<UserTokenConvertPort>()
        val mockUserTokenManagementPort = mockk<UserTokenManagementPort>(relaxed = true)

        val userCommandService =
            UserCommandService(
                mockUserTokenConvertPort,
                mockUserAuthManagementPort,
                mockUserManagementPort,
                mockUserTokenManagementPort,
            )

        given("회원 가입 요청이 들어왔을 때") {
            val successCommand =
                RegisterUserUsecase.Command(
                    "valid",
                    true,
                    true,
                    true,
                    LocalDate.now(),
                    "test",
                )
            every { mockUserTokenManagementPort.isExistsToken("valid") } returns true
            every { mockUserTokenConvertPort.resolveRegisterToken("valid") } returns
                UserClaims.Register(
                    socialId = "123",
                    socialLoginProvider = SocialLoginProvider.KAKAO,
                    username = "test",
                    profileImage = "profileImage",
                    email = "email",
                )
            every { mockUserAuthManagementPort.isExistsUserAuth("123", SocialLoginProvider.KAKAO) } returns false
            every { mockUserTokenConvertPort.generateAccessToken(any()) } returns "accessToken"
            every { mockUserTokenConvertPort.generateRefreshToken(any()) } returns "refreshToken"
            `when`("회원 가입이 성공하면") {
                val response = userCommandService.registerUser(successCommand)
                then("access token과 refresh token을 반환한다.") {
                    response.accessToken.isNotEmpty() shouldBe true
                    response.refreshToken.isNotEmpty() shouldBe true
                    verify { mockUserManagementPort.save(any()) }
                }
            }

            every { mockUserTokenConvertPort.resolveRegisterToken("duplicate") } returns
                UserClaims.Register(
                    socialId = "duplicate",
                    socialLoginProvider = SocialLoginProvider.KAKAO,
                    username = "test",
                    profileImage = "profileImage",
                    email = "email",
                )
            every { mockUserTokenManagementPort.isExistsToken("duplicate") } returns true
            every { mockUserAuthManagementPort.isExistsUserAuth("duplicate", SocialLoginProvider.KAKAO) } returns true
            `when`("중복 가입 요청이 들어왔을 때") {
                val failCommand =
                    RegisterUserUsecase.Command(
                        "duplicate",
                        true,
                        true,
                        true,
                        LocalDate.now(),
                        "test",
                    )
                then("UserAlreadyRegisteredException 예외가 발생한다.") {
                    shouldThrow<UserException.UserAlreadyRegisteredException> {
                        userCommandService.registerUser(failCommand)
                    }
                }
            }

            every { mockUserTokenManagementPort.isExistsToken("invalid") } returns true
            every { mockUserTokenConvertPort.resolveRegisterToken("invalid") } throws IllegalArgumentException("Invalid token")
            `when`("register token이 유요하지 않다면") {
                val failCommandWithoutRegisterToken =
                    RegisterUserUsecase.Command("invalid", true, true, true, LocalDate.now(), "test")
                then("예외가 발생한다.") {
                    shouldThrow<Exception> {
                        userCommandService.registerUser(failCommandWithoutRegisterToken)
                    }
                }
            }

            every { mockUserTokenManagementPort.isExistsToken("non-saved") } returns false
            `when`("register token이 존재하지 않는다면") {
                val failCommandWithoutRegisterToken =
                    RegisterUserUsecase.Command("non-saved", true, true, true, LocalDate.now(), "test")
                then("UserPermissionDeniedException 예외가 발생한다.") {
                    shouldThrow<UserException.UserPermissionDeniedException> {
                        userCommandService.registerUser(failCommandWithoutRegisterToken)
                    }
                }
            }

            every { mockUserTokenConvertPort.resolveRegisterToken("valid") } returns
                UserClaims.Register(
                    socialId = "123",
                    socialLoginProvider = SocialLoginProvider.KAKAO,
                    username = "test",
                    profileImage = "profileImage",
                    email = "email",
                )
            every { mockUserTokenManagementPort.isExistsToken("valid") } returns true
            every { mockUserAuthManagementPort.isExistsUserAuth("123", SocialLoginProvider.KAKAO) } returns false
            `when`("서비스 동의를 하지 않았다면") {
                val failCommandWithoutServicePermission =
                    RegisterUserUsecase.Command("valid", false, true, true, LocalDate.now(), "test")
                then("InvalidPropertyException 예외가 발생한다.") {
                    shouldThrow<DefaultException.InvalidDefaultException> {
                        userCommandService.registerUser(failCommandWithoutServicePermission)
                    }
                }
            }

            `when`("개인정보 동의를 하지 않았다면") {
                val failCommandWithoutPrivatePermission =
                    RegisterUserUsecase.Command("valid", true, false, true, LocalDate.now(), "test")
                then("InvalidPropertyException 예외가 발생한다.") {
                    shouldThrow<DefaultException.InvalidDefaultException> {
                        userCommandService.registerUser(failCommandWithoutPrivatePermission)
                    }
                }
            }
        }

        given("사용자 정보 수정 요청이 들어올 때") {
            val user =
                UserFixture.createUser().apply {
                    this.birthday = null
                }

            val command =
                UpdateUserUsecase.Command.Birthday(
                    userId = user.id.value,
                    birthday = LocalDate.now(),
                )
            every { mockUserManagementPort.getUserNotNull(any()) } returns user
            `when`("생일 수정일 경우") {
                userCommandService.executeFor(command)
                then("생일이 수정된다.") {
                    user.birthday.shouldNotBeNull()
                    verify { mockUserManagementPort.save(any()) }
                }
            }
        }
    })
