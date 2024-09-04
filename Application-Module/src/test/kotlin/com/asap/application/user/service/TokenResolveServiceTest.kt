package com.asap.application.user.service

import com.asap.application.user.port.out.UserTokenConvertPort
import com.asap.application.user.vo.UserClaims
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class TokenResolveServiceTest: BehaviorSpec({

    val userTokenConvertPort = mockk<UserTokenConvertPort>()


    val tokenResolveService = TokenResolveService(
        userTokenConvertPort
    )

    given("토큰 파싱 요청이 들어왔을 때") {
        val accessToken = "accessToken"
        val userClaims = UserClaims.Access(
            userId = "userId"
        )
        every { userTokenConvertPort.resolveAccessToken(accessToken) } returns userClaims
        `when`("토큰이 주어진다면") {
            val response = tokenResolveService.resolveAccessToken(accessToken)
            then("토큰을 파싱하여 유저 아이디를 반환한다") {
                response.userId shouldBe userClaims.userId
            }
        }
    }

})