package com.asap.domain.space.service

import com.asap.common.exception.DefaultException
import com.asap.domain.common.DomainId
import com.asap.domain.space.entity.Space

class SpaceIndexValidator {

    fun validate(spaces: List<Space>, validateIndex: Map<DomainId, Int>){
        val spaceSize = spaces.size
        val indexSet = validateIndex.values.toSet()
        if (indexSet.size != spaceSize) {
            throw DefaultException.InvalidArgumentException("요청 인덱스가 기존 엔덱스와 일치하지 않습니다.")
        }
        if (indexSet.minOrNull() != 0 || indexSet.maxOrNull() != spaceSize - 1) {
            throw DefaultException.InvalidArgumentException("인덱스 범위가 기존 인덱스 범위와 일치하지 않습니다.")
        }
        spaces.forEach {
            if(validateIndex.containsKey(it.id).not()){
                throw DefaultException.InvalidArgumentException("사용자의 스페이스에 포함되지 않은 요청이 있습니다.")
            }
        }
    }

}