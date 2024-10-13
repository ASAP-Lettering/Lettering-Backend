package com.asap.common.page

data class ListResponse<T> internal constructor(
    val content: List<T>,
    val size: Int,
) {
    companion object {
        fun <T> of(content: List<T>): ListResponse<T> = ListResponse(content, content.size)
    }
}
