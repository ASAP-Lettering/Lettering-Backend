package com.asap.domain.common

import com.fasterxml.uuid.Generators

data class DomainId(
    val id: String
) {
    companion object{
        fun generate(): DomainId {
            return DomainId(Generators.timeBasedEpochGenerator().generate().toString())
        }
    }
}