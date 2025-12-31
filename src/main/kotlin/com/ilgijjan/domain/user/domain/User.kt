package com.ilgijjan.domain.user.domain

import com.ilgijjan.common.domain.BaseEntity
import com.ilgijjan.domain.auth.domain.OauthInfo
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_user_provider_info",
            columnNames = ["provider", "provider_id"]
        )
    ]
)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    name: String,

    character: Character,

    isNotificationEnabled: Boolean = true,

    @Embedded
    val oauthInfo: OauthInfo
) : BaseEntity() {

    var name: String = name
        private set

    @Enumerated(EnumType.STRING)
    @Column(name = "`character`")
    var character: Character = character
        private set

    var isNotificationEnabled: Boolean = isNotificationEnabled
        private set

    fun updateName(name: String) {
        require(name.isNotBlank()) { "이름은 비어 있을 수 없습니다." }
        this.name = name
    }

    fun updateCharacter(character: Character) {
        this.character = character
    }

    fun updateNotification(isEnabled: Boolean) {
        this.isNotificationEnabled = isEnabled
    }

    fun isOnboarded(): Boolean {
        return !this.name.startsWith("tmp_")
    }
}
