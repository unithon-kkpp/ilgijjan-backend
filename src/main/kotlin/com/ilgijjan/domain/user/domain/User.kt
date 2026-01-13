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
import java.time.LocalDateTime

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

    var name: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "`character`")
    var character: Character? = null,

    var isNotificationEnabled: Boolean = true,

    @Embedded
    val oauthInfo: OauthInfo,

    var deletedAt: LocalDateTime? = null
) : BaseEntity() {
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

    fun getMaskedName(): String {
        if (this.deletedAt != null) return "탈퇴한 유저"
        val currentName = this.name
        checkNotNull(currentName) { "데이터 정합성 오류: 유저(id=${this.id})의 이름이 없습니다.(회원가입 미완료)" }
        return currentName
    }

    fun withdraw() {
        this.deletedAt = LocalDateTime.now()
    }
}
