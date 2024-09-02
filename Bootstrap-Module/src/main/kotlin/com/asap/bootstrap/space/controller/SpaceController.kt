package com.asap.bootstrap.space.controller

import com.asap.bootstrap.space.api.SpaceApi
import com.asap.bootstrap.space.dto.*
import org.springframework.web.bind.annotation.RestController

@RestController
class SpaceController(

): SpaceApi {

    override fun getMainSpaceId(): MainSpaceInfoResponse {
        return MainSpaceInfoResponse("mainSpaceId")
    }

    override fun getSpaces(): GetAllSpaceResponse {
        return GetAllSpaceResponse(listOf(
            GetAllSpaceResponse.SpaceDetail(
                "spaceName",
                0,
                true,
                0,
                "spaceId"
            )
        ))
    }

    override fun createSpace(request: CreateSpaceRequest) {

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