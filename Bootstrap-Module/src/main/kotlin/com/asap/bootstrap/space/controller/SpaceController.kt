package com.asap.bootstrap.space.controller

import com.asap.application.space.port.`in`.*
import com.asap.bootstrap.space.api.SpaceApi
import com.asap.bootstrap.space.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceController(
    private val getMainSpaceUsecase: GetMainSpaceUsecase,
    private val spaceCreateUsecase: CreateSpaceUsecase,
    private val updateSpaceNameUsecase: UpdateSpaceNameUsecase,
    private val getSpaceUsecase: GetSpaceUsecase,
    private val deleteSpaceUsecase: DeleteSpaceUsecase,
    private val updateSpaceIndexUsecase: UpdateSpaceIndexUsecase,
) : SpaceApi {
    override fun getMainSpace(userId: String): MainSpaceInfoResponse {
        val response =
            getMainSpaceUsecase.get(
                GetMainSpaceUsecase.Query(userId),
            )
        return MainSpaceInfoResponse(response.id, response.username, response.templateType, response.spaceName)
    }

    override fun getSpaces(userId: String): GetAllSpaceResponse {
        val response =
            getSpaceUsecase.getAll(
                GetSpaceUsecase.GetAllQuery(userId),
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
            CreateSpaceUsecase.Command(
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
        updateSpaceNameUsecase.update(
            UpdateSpaceNameUsecase.Command(
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
        deleteSpaceUsecase.deleteOne(
            DeleteSpaceUsecase.DeleteOneCommand(
                userId = userId,
                spaceId = spaceId,
            ),
        )
    }

    override fun updateSpaceOrder(
        request: UpdateSpaceOrderRequest,
        userId: String,
    ) {
        updateSpaceIndexUsecase.update(
            UpdateSpaceIndexUsecase.Command(
                userId = userId,
                orders = request.orders.map { UpdateSpaceIndexUsecase.Command.SpaceOrder(it.spaceId, it.index) },
            ),
        )
    }

    override fun deleteSpaces(
        request: DeleteMultipleSpacesRequest,
        userId: String,
    ) {
        deleteSpaceUsecase.deleteAllBy(
            DeleteSpaceUsecase.DeleteAllCommand(
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
            getSpaceUsecase.get(
                GetSpaceUsecase.GetQuery(
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
