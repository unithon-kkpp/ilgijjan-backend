package com.ilgijjan.common.utils

object StringUtil {
    fun removeWhitespaces(text: String): String {
        if (text.isEmpty()) return ""
        return text.replace(Regex("\\s"), "")
    }

    fun maskSensitiveFields(json: String): String {
        if (json.isBlank()) return json

        val sensitiveFields = "accessToken|refreshToken|token"
        val regex = """("($sensitiveFields)"\s*:\s*")([^"]+)(")""".toRegex(RegexOption.IGNORE_CASE)

        return json.replace(regex) { result ->
            "${result.groupValues[1]}*******${result.groupValues[4]}"
        }
    }
}
