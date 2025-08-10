package com.ilgijjan.integration.text

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/text")
class TextController(
    private val textRefiner: TextRefiner
) {

    @PostMapping("/refine")
    fun refineText(@RequestBody text: String): String {
        return textRefiner.refineText(text)
    }
}