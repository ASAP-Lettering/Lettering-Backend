package com.asap.application.space.service

import com.asap.application.space.port.`in`.MainSpaceGetUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.MainSpace
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SpaceQueryServiceTest : BehaviorSpec({

    val spaceManagementPort = mockk<SpaceManagementPort>()


    val spaceQueryService = SpaceQueryService(
        spaceManagementPort,
    )

    given("메인 스페이스 조회 요청이 들어왔을 때") {
        val mainSpace = MainSpace(
            id = DomainId.generate()
        )
        val query = MainSpaceGetUsecase.Query(
            userId = "userId"
        )
        every { spaceManagementPort.getMainSpace(any()) } returns mainSpace
        `when`("유저 아이디가 주어진다면") {
            val response = spaceQueryService.get(query)
            then("메인 스페이스를 반환한다") {
                response.id shouldBe mainSpace.id.value
            }
        }
    }
})