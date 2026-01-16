package com.ilgijjan.common.config

import com.ilgijjan.common.log.LogContextInterceptor
import com.ilgijjan.common.resolver.LoginUserArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val loginUserArgumentResolver: LoginUserArgumentResolver,
    private val logContextInterceptor: LogContextInterceptor
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(loginUserArgumentResolver)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(logContextInterceptor)
            .addPathPatterns("/**")
            .excludePathPatterns(
                "/api/storage/upload",
                "/actuator/**",
                "/swagger-ui/**", "/v3/api-docs/**",
                "/error",
                "/favicon.ico"
            )
    }
}
