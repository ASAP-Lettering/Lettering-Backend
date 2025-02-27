package com.asap.application.space.service

import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.space.port.`in`.GetMainSpaceUsecase
import com.asap.application.space.port.`in`.GetSpaceUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.SpaceFixture
import com.asap.domain.UserFixture
import com.asap.domain.common.DomainId
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SpaceQueryServiceTest :
    BehaviorSpec({

        val spaceManagementPort = mockk<SpaceManagementPort>()
        val userManagementPort = mockk<UserManagementPort>()
        val spaceLetterManagementPort = mockk<SpaceLetterManagementPort>()

        val spaceQueryService =
            SpaceQueryService(
                spaceManagementPort,
                userManagementPort,
                spaceLetterManagementPort,
            )

        given("메인 스페이스 조회 요청이 들어왔을 때") {
            val user = UserFixture.createUser()
            val query =
                GetMainSpaceUsecase.Query(
                    userId = user.id.value,
                )
            val space = SpaceFixture.createSpace(
                userId = user.id,
            )
            every { spaceManagementPort.getMainSpace(any()) } returns space
            every { userManagementPort.getUserNotNull(any()) } returns user
            every { spaceManagementPort.getSpaceNotNull(any(), any()) } returns space
            `when`("유저 아이디가 주어진다면") {
                val response = spaceQueryService.get(query)
                then("메인 스페이스를 반환한다") {
                    response.id shouldBe space.id.value
                    response.username shouldBe user.username
                    response.templateType shouldBe space.templateType
                    response.spaceName shouldBe space.name
                }
            }
        }

        given("모든 스페이스 조회 요청이 들어왔을 때") {
            val spaces = (0..2).mapIndexed { index, _ ->
                SpaceFixture.createSpace(
                    userId = DomainId("userId"),
                    index = index,
                )
            }
            val indexedSpaceMap = spaces.associateBy { it.id }
            val query =
                GetSpaceUsecase.GetAllQuery(
                    userId = "userId",
                )
            every { spaceManagementPort.getAllSpaceBy(DomainId(query.userId)) } returns spaces
            every { spaceLetterManagementPort.countSpaceLetterBy(any(), any()) } returns 0
            `when`("유저 아이디가 주어진다면") {
                val response = spaceQueryService.getAll(query)
                then("모든 스페이스를 반환한다") {
                    response.spaces.size shouldBe spaces.size
                    response.spaces.forEach { spaceDetail ->
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

        given("행성 모두 조회 요청이 들어왔을 때") {
            val spaces = (0..2).mapIndexed { index, _ ->
                SpaceFixture.createSpace(
                    userId = DomainId("userId"),
                    index = index,
                )
            }
            val spaceMap = spaces.associateBy { it.id }
            val query =
                GetSpaceUsecase.GetAllQuery(
                    userId = "userId",
                )
            every { spaceManagementPort.getAllSpaceBy(DomainId(query.userId)) } returns spaces
            `when`("유저 아이디가 주어진다면") {
                val response = spaceQueryService.getAll(query)
                then("모든 스페이스를 반환한다") {
                    response.spaces.size shouldBe spaces.size
                    response.spaces.forEach { spaceDetail ->
                        val indexedSpace = spaceMap[DomainId(spaceDetail.spaceId)]
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

        given("행성 조회 요청이 들어왔을 때") {
            val space = SpaceFixture.createSpace(
                id = DomainId("spaceId"),
                userId = DomainId("userId"),
            )
            val query =
                GetSpaceUsecase.GetQuery(
                    userId = "userId",
                    spaceId = space.id.value,
                )
            every { spaceManagementPort.getSpaceNotNull(DomainId(query.userId), DomainId(query.spaceId)) } returns space
            `when`("유저 아이디와 스페이스 아이디가 주어진다면") {
                val response = spaceQueryService.get(query)
                then("스페이스를 반환한다") {
                    response.spaceId shouldBe space.id.value
                    response.spaceName shouldBe space.name
                    response.templateType shouldBe space.templateType
                }
            }
        }
    })
