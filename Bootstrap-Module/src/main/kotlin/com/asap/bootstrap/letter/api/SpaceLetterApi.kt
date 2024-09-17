package com.asap.bootstrap.letter.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import com.asap.bootstrap.letter.dto.MoveLetterToSpaceRequest
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/spaces")
interface SpaceLetterApi {

    @GetMapping("/{spaceId}/letters")
    fun getSpaceLetters(
        @RequestParam page: Int,
        @RequestParam size: Int,
        @PathVariable spaceId: String
    )


    @PutMapping("/letters/{letterId}")
    fun moveLetterToSpace(
        @PathVariable letterId: String,
        @RequestBody request: MoveLetterToSpaceRequest,
        @AccessUser userId: String
    )


    @PutMapping("/letters/{letterId}/independent")
    fun moveLetterToIndependentLetter(
        @PathVariable letterId: String
    )
}