package com.asap.common.page

data class Page<T>(
    val content: List<T>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val page: Int
) {

    companion object{
        fun <T> of(
            content: List<T>,
            totalElements: Long,
            totalPages: Int,
            size: Int,
            page: Int
        ): Page<T> {
            return Page(
                content = content,
                totalElements = totalElements,
                totalPages = totalPages,
                size = size,
                page = page
            )
        }
    }
}