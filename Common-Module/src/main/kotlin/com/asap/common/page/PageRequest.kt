package com.asap.common.page

data class PageRequest(
    val page: Int,
    val size: Int,
    val sorts: Sort? = null,
)
