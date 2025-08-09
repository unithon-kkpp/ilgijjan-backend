package com.tadadiary.diary.domain

import com.tadadiary.common.domain.BaseEntity
import jakarta.persistence.*

@Entity
class Diary (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var text: String,

    @Enumerated(EnumType.STRING)
    var weather: Weather,

    var photoUrl: String? = null,

    var imageUrl: String,

    var musicUrl: String,

    var lyrics: String,

    var mood: Int

) : BaseEntity()
