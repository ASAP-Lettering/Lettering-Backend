package com.asap.application.space.service

import com.asap.application.space.port.`in`.MainSpaceGetUsecase
import com.asap.application.space.port.`in`.SpaceGetUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.IndexedSpace
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.user.entity.User
import com.asap.domain.user.vo.UserPermission
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SpaceQueryServiceTest : BehaviorSpec({

    val spaceManagementPort = mockk<SpaceManagementPort>()
    val userManagementPort = mockk<UserManagementPort>()


    val spaceQueryService = SpaceQueryService(
        spaceManagementPort,
        userManagementPort
    )

    given("메인 스페이스 조회 요청이 들어왔을 때") {
        val mainSpace = MainSpace(
            id = DomainId.generate()
        )
        val query = MainSpaceGetUsecase.Query(
            userId = "userId"
        )
        val user = User(
            id = DomainId("userId"),
            username = "username",
            profileImage = "profileImage",
            permission = UserPermission(
                true,true,true
            ),
            birthday = null
        )
        every { spaceManagementPort.getMainSpace(any()) } returns mainSpace
        every { userManagementPort.getUserNotNull(any()) } returns user
        `when`("유저 아이디가 주어진다면") {
            val response = spaceQueryService.get(query)
            then("메인 스페이스를 반환한다") {
                response.id shouldBe mainSpace.id.value
                response.username shouldBe user.username
            }
        }
    }


    given("모든 스페이스 조회 요청이 들어왔을 때") {
        val indexedSpaces = listOf(
            IndexedSpace(
                id = DomainId.generate(),
                name = "name",
                index = 0,
                userId = DomainId("userId"),
                templateType = 1
            ),
            IndexedSpace(
                id = DomainId.generate(),
                name = "name",
                index = 1,
                userId = DomainId("userId"),
                templateType = 1
            ),
            IndexedSpace(
                id = DomainId.generate(),
                name = "name",
                index = 2,
                userId = DomainId("userId"),
                templateType = 1
            )
        )
        val indexedSpaceMap = indexedSpaces.associateBy { it.id }
        val query = SpaceGetUsecase.GetAllQuery(
            userId = "userId"
        )
        every { spaceManagementPort.getAllIndexedSpace(DomainId(query.userId)) } returns indexedSpaces
        `when`("유저 아이디가 주어진다면") {
            val response = spaceQueryService.getAll(query)
            then("모든 스페이스를 반환한다") {
                response.spaces.size shouldBe indexedSpaces.size
                response.spaces.forEach{ spaceDetail ->
                    val indexedSpace = indexedSpaceMap[DomainId(spaceDetail.spaceId)]
                    indexedSpace.shouldNotBeNull {
                        this.id shouldBe DomainId(spaceDetail.spaceId)
                        this.name shouldBe spaceDetail.spaceName
                        this.index shouldBe spaceDetail.spaceIndex
                        this.userId shouldBe DomainId(query.userId)
                    }
                }
            }
        }
    }


    given("행성 편지를 모두 조회 요청이 들어왔을 때") {
        val indexedSpaces = listOf(
            IndexedSpace(
                id = DomainId.generate(),
                name = "name",
                index = 0,
                userId = DomainId("userId"),
                templateType = 1
            ),
            IndexedSpace(
                id = DomainId.generate(),
                name = "name",
                index = 1,
                userId = DomainId("userId"),
                templateType = 1
            ),
            IndexedSpace(
                id = DomainId.generate(),
                name = "name",
                index = 2,
                userId = DomainId("userId"),
                templateType = 1
            )
        )
        val indexedSpaceMap = indexedSpaces.associateBy { it.id }
        val query = SpaceGetUsecase.GetAllQuery(
            userId = "userId"
        )
        every { spaceManagementPort.getAllIndexedSpace(DomainId(query.userId)) } returns indexedSpaces
        `when`("유저 아이디가 주어진다면") {
            val response = spaceQueryService.getAll(query)
            then("모든 스페이스를 반환한다") {
                response.spaces.size shouldBe indexedSpaces.size
                response.spaces.forEach{ spaceDetail ->
                    val indexedSpace = indexedSpaceMap[DomainId(spaceDetail.spaceId)]
                    indexedSpace.shouldNotBeNull {
                        this.id shouldBe DomainId(spaceDetail.spaceId)
                        this.name shouldBe spaceDetail.spaceName
                        this.index shouldBe spaceDetail.spaceIndex
                        this.userId shouldBe DomainId(query.userId)
                    }
                }
            }
        }
    }
})