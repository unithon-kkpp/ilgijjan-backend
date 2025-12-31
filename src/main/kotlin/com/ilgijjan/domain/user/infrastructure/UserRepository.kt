package com.ilgijjan.domain.user.infrastructure

import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun existsByNameAndIdNot(name: String, id: Long): Boolean

    fun findByOauthInfoProviderAndOauthInfoProviderId(
        provider: OauthProvider,
        providerId: String
    ): User?
}
