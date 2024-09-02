package com.asap.bootstrap.letter.api

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
    fun moveLetter(
        @PathVariable letterId: String
    )


    @PutMapping("/letters/{letterId}/independent")
    fun moveLetterToIndependentLetter(
        @PathVariable letterId: String
    )
}