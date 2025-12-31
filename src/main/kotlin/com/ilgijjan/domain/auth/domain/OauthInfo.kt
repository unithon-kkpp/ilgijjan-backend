package com.ilgijjan.domain.auth.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated

@Embeddable
data class OauthInfo (
    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    val provider: OauthProvider,

    @Column(name = "provider_id")
    val providerId: String
)
