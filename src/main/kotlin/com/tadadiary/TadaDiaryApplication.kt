package com.tadadiary

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TadaDiaryApplication

fun main(args: Array<String>) {
    runApplication<TadaDiaryApplication>(*args)
}
