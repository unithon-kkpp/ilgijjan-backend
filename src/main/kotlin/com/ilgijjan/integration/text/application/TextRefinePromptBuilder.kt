package com.ilgijjan.integration.text.application

import org.springframework.stereotype.Component

@Component
class TextRefinePromptBuilder {
    fun build(text: String): String {
        return "$text Please refine this diary entry into concise English within 80 characters, capturing the core meaning. Output only the refined text without any extra explanations."
    }
}
