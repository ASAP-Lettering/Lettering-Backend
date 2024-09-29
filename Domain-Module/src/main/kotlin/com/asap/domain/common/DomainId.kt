package com.asap.domain.common

import com.fasterxml.uuid.Generators

@JvmInline
value class DomainId(
    val value: String
) {
    companion object{
        fun generate(): DomainId {
            return DomainId(Generators.timeBasedEpochGenerator().generate().toString())
        }
    }
}