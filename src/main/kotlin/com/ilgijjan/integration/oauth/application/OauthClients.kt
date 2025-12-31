package com.ilgijjan.integration.oauth.application

import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.auth.domain.OauthProvider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class OauthClients(
    private val clients: List<OauthClient>
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun getClient(provider: OauthProvider): OauthClient {
        return clients.find { it.supports(provider) }
            ?: run {
                log.error("지원하지 않는 OAuth 공급자입니다. 요청된 Provider: {}, 현재 등록된 Clients: {}",
                    provider,
                    clients.map { it.javaClass.simpleName }
                )
                throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR)
            }
    }
}
