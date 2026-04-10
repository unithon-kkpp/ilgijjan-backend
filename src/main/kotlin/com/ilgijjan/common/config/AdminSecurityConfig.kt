package com.ilgijjan.common.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.web.SecurityFilterChain

@Configuration
@Order(1)
class AdminSecurityConfig(
    @Value("\${admin.allowed-emails}") private val allowedEmailsRaw: String,
    private val clientRegistrationRepository: ClientRegistrationRepository,
) {

    private val allowedEmails: Set<String> by lazy {
        allowedEmailsRaw.split(",").map { it.trim() }.toSet()
    }

    @Bean
    fun adminFilterChain(http: HttpSecurity): SecurityFilterChain {
        val oauth2UserService = DefaultOAuth2UserService()

        val authorizationRequestResolver = DefaultOAuth2AuthorizationRequestResolver(
            clientRegistrationRepository,
            "/admin/oauth2/authorization"
        )
        authorizationRequestResolver.setAuthorizationRequestCustomizer { customizer ->
            customizer.additionalParameters { params -> params["prompt"] = "select_account" }
        }

        http
            .securityMatcher("/admin/**")
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/admin/login-error").permitAll()
                it.anyRequest().authenticated()
            }
            .oauth2Login {
                it.loginPage("/admin/oauth2/authorization/google")
                it.authorizationEndpoint { auth ->
                    auth.baseUri("/admin/oauth2/authorization")
                    auth.authorizationRequestResolver(authorizationRequestResolver)
                }
                it.redirectionEndpoint { redirect ->
                    redirect.baseUri("/admin/login/oauth2/code/*")
                }
                it.userInfoEndpoint { userInfo ->
                    userInfo.userService { request ->
                        val user = oauth2UserService.loadUser(request)
                        val email = user.attributes["email"] as? String
                            ?: throw OAuth2AuthenticationException(OAuth2Error("email_not_found"))
                        if (email !in allowedEmails) {
                            throw OAuth2AuthenticationException(OAuth2Error("access_denied"), "허용되지 않은 이메일: $email")
                        }
                        user
                    }
                }
                it.defaultSuccessUrl("/admin", true)
                it.failureUrl("/admin/login-error")
            }
            .logout {
                it.logoutUrl("/admin/logout")
                    .logoutSuccessUrl("/admin/oauth2/authorization/google")
            }
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->
                    response.sendRedirect("/admin/oauth2/authorization/google")
                }
            }

        return http.build()
    }
}
