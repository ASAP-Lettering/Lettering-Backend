package com.asap.bootstrap.space.controller

import com.asap.application.space.port.`in`.MainSpaceQueryUsecase
import com.asap.application.space.port.`in`.SpaceCreateUsecase
import com.asap.bootstrap.space.api.SpaceApi
import com.asap.bootstrap.space.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceController(
    private val mainSpaceQueryUsecase: MainSpaceQueryUsecase,
    private val spaceCreateUsecase: SpaceCreateUsecase
) : SpaceApi {

    override fun getMainSpace(
        userId: String
    ): MainSpaceInfoResponse {
        val response = mainSpaceQueryUsecase.get(
            MainSpaceQueryUsecase.Query(userId)
        )
        return MainSpaceInfoResponse(response.id)
    }

    override fun getSpaces(): GetAllSpaceResponse {
        return GetAllSpaceResponse(
            listOf(
                GetAllSpaceResponse.SpaceDetail(
                    "spaceName",
                    0,
                    true,
                    0,
                    "spaceId"
                )
            )
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

    override fun updateSpaceName(spaceId: String, request: UpdateSpaceNameRequest) {

    }

    override fun deleteSpace(spaceId: String) {

    }

    override fun updateSpaceOrder(request: UpdateSpaceOrderRequest) {

    }

    override fun deleteSpaces(request: DeleteMultipleSpacesRequest) {

    }
}