package com.ilgijjan.common.utils

object StringUtil {
    fun removeWhitespaces(text: String): String {
        if (text.isEmpty()) return ""
        return text.replace(Regex("\\s"), "")
    }
}
