package com.ilgijjan.common.utils

import java.util.UUID

object IdGenerator {
    fun generateTraceId(): String {
        return UUID.randomUUID().toString().substring(0, 8)
    }
}
