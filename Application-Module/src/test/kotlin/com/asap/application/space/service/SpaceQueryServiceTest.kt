package com.asap.application.space.service

import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.utils.UserUtils
import com.asap.domain.space.entity.MainSpace
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SpaceQueryServiceTest: BehaviorSpec({

    val userUtils = mockk<UserUtils>()
    val spaceManagementPort = mockk<SpaceManagementPort>()


    val spaceQueryService = SpaceQueryService(
        spaceManagementPort,
        userUtils
    )

    given("메인 스페이스 조회 요청이 들어왔을 때") {
        val mainSpace = MainSpace(
            id = "mainSpaceId"
        )
        every { spaceManagementPort.getMainSpace(any()) } returns mainSpace
        every { userUtils.getAccessUserId() } returns "userId"
        `when`("유저 아이디가 주어진다면") {
            val response = spaceQueryService.query()
            then("메인 스페이스를 반환한다") {
                response.id shouldBe mainSpace.id
            }
        }
    }
})