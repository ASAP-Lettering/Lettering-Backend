package com.asap.domain.space.entity

import com.asap.domain.common.DomainId

data class IndexedSpace(
    val id: DomainId,
    val userId: DomainId,
    val name: String,
    var index: Int,
    val templateType: Int,
) {
    fun isMain(): Boolean = index == 0

    fun updateIndex(index: Int)  {
        check(index >= 0) { "Index must be greater than or equal to 0" }
        this.index = index
    }
}
