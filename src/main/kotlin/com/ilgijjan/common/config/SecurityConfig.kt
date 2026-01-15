package com.ilgijjan.common.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.ilgijjan.common.jwt.JwtAuthenticationEntryPoint
import com.ilgijjan.common.jwt.JwtAuthenticationFilter
import com.ilgijjan.common.jwt.JwtTokenProvider
import com.ilgijjan.domain.auth.application.TokenManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider,
    private val authenticationEntryPoint: JwtAuthenticationEntryPoint,
    private val objectMapper: ObjectMapper,
    private val tokenManager: TokenManager
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .httpBasic { it.disable() }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authorizeHttpRequests {
                it.requestMatchers(
                    "/api/auth/login", "api/auth/reissue",
                    "/api/music/**",
                    "/billing/webhooks/**",
                    "/swagger-ui/**", "/v3/api-docs/**",
                    "/actuator/**", "/health"
                ).permitAll()
                    .anyRequest().authenticated()
            }
            .exceptionHandling {
                it.authenticationEntryPoint(authenticationEntryPoint)
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider, objectMapper, tokenManager),
                UsernamePasswordAuthenticationFilter::class.java
            )

        return http.build()
    }
}
