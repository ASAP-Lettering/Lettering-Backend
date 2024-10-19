package com.asap.application.user.service

import com.asap.application.user.port.`in`.GetUserInfoUsecase
import com.asap.application.user.port.out.UserAuthManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.UserFixture
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class UserQueryServiceTest :
    BehaviorSpec({
        val userManagementPort: UserManagementPort = mockk()
        val userAuthManagementPort: UserAuthManagementPort = mockk()

        val userQueryService = UserQueryService(userManagementPort, userAuthManagementPort)

        given("유저 정보 조회 요청이 들어왔을 때") {
            val user = UserFixture.createUser()
            val userAuth = UserFixture.createUserAuth(userId = user.id.value)

            every { userManagementPort.getUserNotNull(user.id) } returns user
            every { userAuthManagementPort.getNotNull(user.id) } returns userAuth
            `when`("유저 정보 조회 요청이 들어왔을 때") {
                val response = userQueryService.getBy(GetUserInfoUsecase.Query.Me(user.id.value))
                then("유저 정보를 반환한다") {
                    with(response) {
                        name shouldBe user.username
                        socialPlatform shouldBe userAuth.socialLoginProvider.name
                        email shouldBe user.email
                        birthday shouldBe user.birthday
                    }
                }
            }
        }
    })
