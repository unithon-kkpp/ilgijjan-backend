package com.ilgijjan.domain.diary.domain

import com.ilgijjan.common.domain.BaseEntity
import com.ilgijjan.common.exception.CustomException
import com.ilgijjan.common.exception.ErrorCode
import com.ilgijjan.domain.user.domain.User
import jakarta.persistence.*

@Entity
class Diary (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    val type: DiaryInputType,

    @Lob
    val text: String?,

    val photoUrl: String?,

    @Enumerated(EnumType.STRING)
    val weather: Weather,

    val mood: Int,

    var imageUrl: String? = null,

    var musicUrl: String? = null,

    @Lob
    var lyrics: String? = null,

    var likeCount: Long = 0,

    var isPublic: Boolean = false,

    @Enumerated(EnumType.STRING)
    var status: DiaryStatus = DiaryStatus.PENDING

) : BaseEntity() {
    fun increaseLikeCount() {
        this.likeCount++
    }

    fun decreaseLikeCount() {
        if (this.likeCount <= 0) {
            throw CustomException(ErrorCode.INVALID_LIKE_COUNT)
        }
        this.likeCount--
    }

    fun publish() {
        this.isPublic = true
    }

    fun unpublish() {
        this.isPublic = false
    }

    fun validateOwner(userId: Long) {
        if (this.user.id != userId) {
            throw CustomException(ErrorCode.NOT_DIARY_OWNER)
        }
    }

    fun setGeneratedContent(imageUrl: String, musicUrl: String, lyrics: String) {
        this.imageUrl = imageUrl
        this.musicUrl = musicUrl
        this.lyrics = lyrics
    }

    fun complete() {
        this.status = DiaryStatus.COMPLETED
    }

    fun fail() {
        this.status = DiaryStatus.FAILED
    }
}
