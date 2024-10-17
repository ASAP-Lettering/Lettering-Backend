package com.asap.persistence.jpa.common

import org.springframework.data.domain.Sort

object PageUtils {
    private fun convertSort(sort: com.asap.common.page.Sort): Sort = Sort.by(Sort.Direction.valueOf(sort.direction.name), sort.property)

    fun com.asap.common.page.Sort?.toJpaSort(): Sort = this?.let { convertSort(it) } ?: Sort.unsorted()

    fun List<com.asap.common.page.Sort>.toJpaSort(): Array<Sort> = this.map { convertSort(it) }.toTypedArray()
}
