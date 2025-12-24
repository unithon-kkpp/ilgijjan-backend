package com.ilgijjan.domain.like.domain

import com.ilgijjan.common.domain.BaseEntity
import com.ilgijjan.domain.diary.domain.Diary
import com.ilgijjan.domain.user.domain.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "likes",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_like_user_diary",
            columnNames = ["user_id", "diary_id"]
        )
    ]
)
class Like (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    val diary: Diary,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: LikeStatus = LikeStatus.ACTIVE,

    ) :  BaseEntity() {

    fun active() {
        this.status = LikeStatus.ACTIVE
    }

    fun delete() {
        this.status = LikeStatus.DELETED
    }

    fun isActive(): Boolean {
        return this.status == LikeStatus.ACTIVE
    }
}
