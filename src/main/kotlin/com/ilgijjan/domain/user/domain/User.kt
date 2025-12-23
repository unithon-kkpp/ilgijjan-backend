package com.ilgijjan.domain.user.domain

import com.ilgijjan.common.domain.BaseEntity
import com.ilgijjan.domain.auth.domain.OauthInfo
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var name: String,

    @Embedded
    val oauthInfo: OauthInfo,

    @Enumerated(EnumType.STRING)
    var avatar : Avatar
) : BaseEntity() {

    fun updateName(name: String) {
        this.name = name
    }

    fun updateAvatar(avatar: Avatar) {
        this.avatar = avatar
    }
}
