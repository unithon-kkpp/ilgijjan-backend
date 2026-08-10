package com.ilgijjan.integration.music.application

import org.springframework.stereotype.Component

@Component
class MusicPromptBuilder {
    fun build(text: String): String {
        return """
            따뜻하고 포근한 한국 어린이 동요를 만들어줘.
            밝고 순수한 아이 목소리로, 아래 일기 내용을 주제로 짧은 한국어 가사를 직접 만들어서 노래로 불러줘.

            일기: $text
        """.trimIndent()
    }
}
