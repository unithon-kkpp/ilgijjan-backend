package com.ilgijjan.domain.auth.domain

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class OauthInfo (
    @Enumerated(EnumType.STRING)
    val provider: OauthProvider,

    val providerId: String
)
