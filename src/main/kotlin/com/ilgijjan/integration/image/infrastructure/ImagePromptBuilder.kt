package com.ilgijjan.integration.image.infrastructure

import com.ilgijjan.domain.diary.domain.Weather
import org.springframework.stereotype.Component

@Component
class ImagePromptBuilder {
    fun build(text: String, weather: Weather): String {
        val weatherStr = weather.name.lowercase()
        return """
            Generate an image directly based on the details below.

            [Content]
            Story: "$text"
            Weather: "$weatherStr"
            Style: Cute children's storybook illustration.
            Ratio: Square (1:1)

            [CRITICAL INSTRUCTION]
            1. Output ONLY the image.
            2. DO NOT generate any text, conversation, or introduction (e.g., "Here is the image").
            3. JUST GENERATE THE IMAGE DATA.
        """.trimIndent()
    }
}
