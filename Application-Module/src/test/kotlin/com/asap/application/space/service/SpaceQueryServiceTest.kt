package com.asap.application.space.service

import com.asap.application.letter.port.out.SpaceLetterManagementPort
import com.asap.application.space.port.`in`.MainSpaceGetUsecase
import com.asap.application.space.port.`in`.SpaceGetUsecase
import com.asap.application.space.port.out.SpaceManagementPort
import com.asap.application.user.port.out.UserManagementPort
import com.asap.domain.UserFixture
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.IndexedSpace
import com.asap.domain.space.entity.MainSpace
import com.asap.domain.space.entity.Space
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
            val mainSpace =
                MainSpace(
                    id = DomainId.generate(),
                )
            val query =
                MainSpaceGetUsecase.Query(
                    userId = "userId",
                )
            val user = UserFixture.createUser("userId")
            val space =
                Space(
                    id = mainSpace.id,
                    name = "name",
                    userId = user.id,
                    templateType = 1,
                )
            every { spaceManagementPort.getMainSpace(any()) } returns mainSpace
            every { userManagementPort.getUserNotNull(any()) } returns user
            every { spaceManagementPort.getSpaceNotNull(any(), any()) } returns space
            `when`("유저 아이디가 주어진다면") {
                val response = spaceQueryService.get(query)
                then("메인 스페이스를 반환한다") {
                    response.id shouldBe mainSpace.id.value
                    response.username shouldBe user.username
                    response.templateType shouldBe space.templateType
                    response.spaceName shouldBe space.name
                }
            }
        }

        given("모든 스페이스 조회 요청이 들어왔을 때") {
            val indexedSpaces =
                listOf(
                    IndexedSpace(
                        id = DomainId.generate(),
                        name = "name",
                        index = 0,
                        userId = DomainId("userId"),
                        templateType = 1,
                    ),
                    IndexedSpace(
                        id = DomainId.generate(),
                        name = "name",
                        index = 1,
                        userId = DomainId("userId"),
                        templateType = 1,
                    ),
                    IndexedSpace(
                        id = DomainId.generate(),
                        name = "name",
                        index = 2,
                        userId = DomainId("userId"),
                        templateType = 1,
                    ),
                )
            val indexedSpaceMap = indexedSpaces.associateBy { it.id }
            val query =
                SpaceGetUsecase.GetAllQuery(
                    userId = "userId",
                )
            every { spaceManagementPort.getAllIndexedSpace(DomainId(query.userId)) } returns indexedSpaces
            every { spaceLetterManagementPort.countSpaceLetterBy(any(), any()) } returns 0
            `when`("유저 아이디가 주어진다면") {
                val response = spaceQueryService.getAll(query)
                then("모든 스페이스를 반환한다") {
                    response.spaces.size shouldBe indexedSpaces.size
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
            val indexedSpaces =
                listOf(
                    IndexedSpace(
                        id = DomainId.generate(),
                        name = "name",
                        index = 0,
                        userId = DomainId("userId"),
                        templateType = 1,
                    ),
                    IndexedSpace(
                        id = DomainId.generate(),
                        name = "name",
                        index = 1,
                        userId = DomainId("userId"),
                        templateType = 1,
                    ),
                    IndexedSpace(
                        id = DomainId.generate(),
                        name = "name",
                        index = 2,
                        userId = DomainId("userId"),
                        templateType = 1,
                    ),
                )
            val indexedSpaceMap = indexedSpaces.associateBy { it.id }
            val query =
                SpaceGetUsecase.GetAllQuery(
                    userId = "userId",
                )
            every { spaceManagementPort.getAllIndexedSpace(DomainId(query.userId)) } returns indexedSpaces
            `when`("유저 아이디가 주어진다면") {
                val response = spaceQueryService.getAll(query)
                then("모든 스페이스를 반환한다") {
                    response.spaces.size shouldBe indexedSpaces.size
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

        given("행성 조회 요청이 들어왔을 때") {
            val space =
                Space(
                    id = DomainId.generate(),
                    name = "name",
                    userId = DomainId("userId"),
                    templateType = 1,
                )
            val query =
                SpaceGetUsecase.GetQuery(
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
