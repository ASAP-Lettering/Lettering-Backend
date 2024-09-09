package com.asap.domain.space.entity

import com.asap.domain.common.DomainId

data class IndexedSpace(
    val id: DomainId,
    val userId: DomainId,
    val name: String,
    val index: Int,
    val templateType: Int
) {

    fun isMain(): Boolean {
        return index == 0
    }

    fun updateIndex(index: Int): IndexedSpace {
        return this.copy(index = index)
    }
}