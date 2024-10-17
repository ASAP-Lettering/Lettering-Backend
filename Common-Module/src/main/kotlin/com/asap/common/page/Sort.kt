package com.asap.common.page

data class Sort(
    val property: String,
    val direction: Direction,
) {
    enum class Direction {
        ASC,
        DESC,
    }
}
