package com.ilgijjan.integration.oauth.infrastructure

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.auth.application.OauthCommand
import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.integration.oauth.application.OauthClient
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient
import kotlin.jvm.java

@Component
class KakaoOauthClient(
    restClientBuilder: RestClient.Builder
) : OauthClient {
    private val log = LoggerFactory.getLogger(javaClass)
    private val restClient = restClientBuilder.build()

    override fun supports(provider: OauthProvider) = provider == OauthProvider.KAKAO

    override fun getProviderId(command: OauthCommand): String {
        val accessToken = checkNotNull(command.accessToken) { "AccessToken must not be null" }

        return try {
            val response = restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .headers { it.setBearerAuth(accessToken) }
                .retrieve()
                .body(KakaoUserInfoResponse::class.java)
            response?.id?.toString() ?: throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        } catch (e: HttpClientErrorException) {
            log.warn("Kakao Error Body: ${e.responseBodyAsString}")
            throw CustomException(ErrorCode.INVALID_KAKAO_TOKEN)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        }
    }

    override fun logout(command: OauthCommand) {
        val accessToken = checkNotNull(command.accessToken) { "accessToken must not be null" }

        try {
            restClient.post()
                .uri("https://kapi.kakao.com/v1/user/logout")
                .headers {
                    it.setBearerAuth(accessToken)
                    it.contentType = MediaType.APPLICATION_FORM_URLENCODED
                }
                .retrieve()
                .toBodilessEntity()
        } catch (e: HttpClientErrorException) {
            log.warn("Kakao Logout Error Body: ${e.responseBodyAsString}")
            throw CustomException(ErrorCode.INVALID_KAKAO_TOKEN)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        }
    }

    override fun unlink(command: OauthCommand) {
        val accessToken = checkNotNull(command.accessToken) { "accessToken must not be null" }

        try {
            restClient.post()
                .uri("https://kapi.kakao.com/v1/user/unlink")
                .headers {
                    it.setBearerAuth(accessToken)
                    it.contentType = MediaType.APPLICATION_FORM_URLENCODED
                }
                .retrieve()
                .toBodilessEntity()
        } catch (e: HttpClientErrorException) {
            log.warn("Kakao Unlink Error Body: ${e.responseBodyAsString}")
            throw CustomException(ErrorCode.INVALID_KAKAO_TOKEN)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.KAKAO_SERVER_ERROR)
        }
    }
}
