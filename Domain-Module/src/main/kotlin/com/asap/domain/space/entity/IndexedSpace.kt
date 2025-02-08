package com.asap.domain.space.entity

import com.asap.domain.common.Aggregate
import com.asap.domain.common.DomainId

class IndexedSpace(
    id: DomainId,
    val userId: DomainId,
    val name: String,
    var index: Int,
    val templateType: Int,
    var isMain: Boolean = false,
) : Aggregate<IndexedSpace>(id) {
    fun updateIndex(index: Int) {
        check(index >= 0) { "Index must be greater than or equal to 0" }
        this.index = index
    }
}
