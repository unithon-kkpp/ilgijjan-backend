package com.ilgijjan.integration.oauth.infrastructure

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.auth.application.OauthCommand
import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.integration.oauth.application.OauthClient
import org.slf4j.LoggerFactory
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import kotlin.jvm.java

@Component
class KakaoOauthClient(
    private val restTemplate: RestTemplate
) : OauthClient {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun supports(provider: OauthProvider) = provider == OauthProvider.KAKAO

    override fun getProviderId(command: OauthCommand): String {
        val accessToken = checkNotNull(command.accessToken) { "AccessToken must not be null" }

        return try {
            val headers = HttpHeaders().apply { setBearerAuth(accessToken) }
            val response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                HttpEntity<Unit>(headers),
                KakaoUserInfoResponse::class.java
            )
            response.body?.id?.toString() ?: throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        } catch (e: HttpClientErrorException) {
            log.warn("Kakao Error Body: ${e.responseBodyAsString}")
            throw CustomException(ErrorCode.INVALID_KAKAO_TOKEN)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        }
    }
}
