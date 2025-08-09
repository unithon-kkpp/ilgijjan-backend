package com.ilgijjan

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.servers.Server
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing

@EnableJpaAuditing
@SpringBootApplication
@OpenAPIDefinition(
    servers = [
        Server(url = "https://ilgijjan.store", description = "Production Server"),
        Server(url = "http://localhost:8080", description = "Local Development Server")
    ]
)
class IlgijjanApplication

fun main(args: Array<String>) {
    runApplication<IlgijjanApplication>(*args)
}
