package com.ilgijjan.domain.diary.domain

import com.ilgijjan.common.domain.BaseEntity
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

    @Lob
    var text: String,

    @Enumerated(EnumType.STRING)
    var weather: Weather,

    var photoUrl: String? = null,

    var imageUrl: String,

    var musicUrl: String,

    @Lob
    var lyrics: String,

    var mood: Int

) : BaseEntity()
