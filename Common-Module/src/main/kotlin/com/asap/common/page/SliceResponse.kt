package com.asap.common.page

data class SliceResponse<T> internal constructor(
    val content: List<T>,
    val size: Int,
    val number: Int,
    val hasNext: Boolean
) {

    companion object{
        fun <T> of(content: List<T>, size: Int, number: Int, hasNext: Boolean): SliceResponse<T> {
            return SliceResponse(content, size, number, hasNext)
        }
    }
}