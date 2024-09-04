package com.asap.common.security

import java.security.Principal

interface Authentication<T>: Principal {

    fun getDetails(): T
}