package com.ilgijjan.domain.like.application

import com.ilgijjan.domain.like.domain.Like
import org.springframework.stereotype.Component

@Component
class LikeUpdater {
    fun active(like: Like) {
        like.active()
    }
}
