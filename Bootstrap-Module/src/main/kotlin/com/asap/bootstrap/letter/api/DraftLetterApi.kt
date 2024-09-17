package com.asap.bootstrap.letter.api

import com.asap.bootstrap.common.security.annotation.AccessUser
import org.springframework.web.bind.annotation.*

@RequestMapping("/api/v1/letters/drafts")
interface DraftLetterApi {

    @PostMapping("/key")
    fun getDraftKey(
        @AccessUser userId: String
    )

    @PostMapping("/{draftId}")
    fun updateDraft(
        @PathVariable draftId: String,
        @AccessUser userId: String
    )

    @GetMapping()
    fun getAllDrafts(
        @AccessUser userId: String
    )

    @DeleteMapping("/{draftId}")
    fun deleteDraft(
        @PathVariable draftId: String,
        @AccessUser userId: String
    )

    
}