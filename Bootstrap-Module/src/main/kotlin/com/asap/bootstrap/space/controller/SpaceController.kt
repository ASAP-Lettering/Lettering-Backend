package com.asap.bootstrap.space.controller

import com.asap.application.space.port.`in`.MainSpaceGetUsecase
import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.application.space.port.`in`.SpaceGetUsecase
import com.asap.application.space.port.`in`.SpaceUpdateNameUsecase
import com.asap.bootstrap.space.api.SpaceApi
import com.asap.bootstrap.space.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceController(
    private val mainSpaceGetUsecase: MainSpaceGetUsecase,
    private val spaceCreateUsecase: SpaceCreateUsecase,
    private val spaceUpdateNameUsecase: SpaceUpdateNameUsecase,
    private val spaceGetUsecase: SpaceGetUsecase
) : SpaceApi {

    override fun getMainSpace(
        userId: String
    ): MainSpaceInfoResponse {
        val response = mainSpaceGetUsecase.get(
            MainSpaceGetUsecase.Query(userId)
        )
        return MainSpaceInfoResponse(response.id)
    }

    override fun getSpaces(
        userId: String
    ): GetAllSpaceResponse {
        val response = spaceGetUsecase.getAll(
            SpaceGetUsecase.GetAllQuery(userId)
        )
        return GetAllSpaceResponse(
            response.spaces.map {
                GetAllSpaceResponse.SpaceDetail(
                    it.spaceName,
                    it.letterCount,
                    it.isMainSpace,
                    it.spaceIndex,
                    it.spaceId
                )
            }
        )
    }

    override fun createSpace(
        request: CreateSpaceRequest,
        userId: String
    ) {
        spaceCreateUsecase.create(
            SpaceCreateUsecase.Command(
                userId,
                request.spaceName,
                request.templateType
            )
        )
    }

    override fun updateSpaceName(
        spaceId: String,
        request: UpdateSpaceNameRequest,
        userId: String
    ) {
        spaceUpdateNameUsecase.update(
            SpaceUpdateNameUsecase.Command(
                userId = userId,
                spaceId = spaceId,
                name = request.spaceName
            )
        )
    }

    override fun deleteSpace(spaceId: String) {

    }

    override fun updateSpaceOrder(request: UpdateSpaceOrderRequest) {

    }

    override fun deleteSpaces(request: DeleteMultipleSpacesRequest) {

    }
}