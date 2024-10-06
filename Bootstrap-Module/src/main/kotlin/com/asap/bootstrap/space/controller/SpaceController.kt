package com.asap.bootstrap.space.controller

import com.asap.application.space.port.`in`.*
import com.asap.bootstrap.space.api.SpaceApi
import com.asap.bootstrap.space.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceController(
    private val mainSpaceGetUsecase: MainSpaceGetUsecase,
    private val spaceCreateUsecase: SpaceCreateUsecase,
    private val spaceUpdateNameUsecase: SpaceUpdateNameUsecase,
    private val spaceGetUsecase: SpaceGetUsecase,
    private val spaceDeleteUsecase: SpaceDeleteUsecase,
    private val spaceUpdateIndexUsecase: SpaceUpdateIndexUsecase,
) : SpaceApi {
    override fun getMainSpace(userId: String): MainSpaceInfoResponse {
        val response =
            mainSpaceGetUsecase.get(
                MainSpaceGetUsecase.Query(userId),
            )
        return MainSpaceInfoResponse(response.id, response.username, response.templateType, response.spaceName)
    }

    override fun getSpaces(userId: String): GetAllSpaceResponse {
        val response =
            spaceGetUsecase.getAll(
                SpaceGetUsecase.GetAllQuery(userId),
            )
        return GetAllSpaceResponse(
            response.spaces.map {
                GetAllSpaceResponse.SpaceDetail(
                    it.spaceName,
                    it.letterCount,
                    it.isMainSpace,
                    it.spaceIndex,
                    it.spaceId,
                )
            },
        )
    }

    override fun createSpace(
        request: CreateSpaceRequest,
        userId: String,
    ) {
        spaceCreateUsecase.create(
            SpaceCreateUsecase.Command(
                userId,
                request.spaceName,
                request.templateType,
            ),
        )
    }

    override fun updateSpaceName(
        spaceId: String,
        request: UpdateSpaceNameRequest,
        userId: String,
    ) {
        spaceUpdateNameUsecase.update(
            SpaceUpdateNameUsecase.Command(
                userId = userId,
                spaceId = spaceId,
                name = request.spaceName,
            ),
        )
    }

    override fun deleteSpace(
        spaceId: String,
        userId: String,
    ) {
        spaceDeleteUsecase.deleteOne(
            SpaceDeleteUsecase.DeleteOneCommand(
                userId = userId,
                spaceId = spaceId,
            ),
        )
    }

    override fun updateSpaceOrder(
        request: UpdateSpaceOrderRequest,
        userId: String,
    ) {
        spaceUpdateIndexUsecase.update(
            SpaceUpdateIndexUsecase.Command(
                userId = userId,
                orders = request.orders.map { SpaceUpdateIndexUsecase.Command.SpaceOrder(it.spaceId, it.index) },
            ),
        )
    }

    override fun deleteSpaces(
        request: DeleteMultipleSpacesRequest,
        userId: String,
    ) {
        spaceDeleteUsecase.deleteAll(
            SpaceDeleteUsecase.DeleteAllCommand(
                userId = userId,
                spaceIds = request.spaceIds,
            ),
        )
    }

    override fun getSpace(
        spaceId: String,
        userId: String,
    ): GetSpaceResponse {
        val response =
            spaceGetUsecase.get(
                SpaceGetUsecase.GetQuery(
                    userId = userId,
                    spaceId = spaceId,
                ),
            )
        return GetSpaceResponse(
            spaceId = response.spaceId,
            templateType = response.templateType,
            spaceName = response.spaceName,
        )
    }
}
