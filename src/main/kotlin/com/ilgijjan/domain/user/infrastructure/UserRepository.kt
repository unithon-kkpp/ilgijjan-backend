package com.ilgijjan.domain.user.infrastructure

import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {

    fun existsByNameAndIdNot(name: String, id: Long): Boolean

    fun findByName(name: String): User?

    fun findByOauthInfoProviderAndOauthInfoProviderId(
        provider: OauthProvider,
        providerId: String
    ): User?

    @Query(
        value = "SELECT * FROM users WHERE provider = :provider AND provider_id = :providerId AND deleted_at IS NOT NULL",
        nativeQuery = true
    )
    fun findDeletedByProviderAndProviderId(
        @Param("provider") provider: String,
        @Param("providerId") providerId: String
    ): User?
}
