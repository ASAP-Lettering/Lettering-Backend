package com.asap.common.page

data class PageResponse<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val page: Int
) {

    companion object {
        fun <T> of(
            content: List<T>,
            totalElements: Long,
            totalPages: Int,
            size: Int,
            page: Int
        ): PageResponse<T> {
            return PageResponse(
                content = content,
                totalElements = totalElements,
                totalPages = totalPages,
                size = size,
                page = page
            )
        }
    }
}