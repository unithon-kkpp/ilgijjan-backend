package com.ilgijjan.domain.like.application

import com.ilgijjan.domain.like.domain.Like
import org.springframework.stereotype.Component

@Component
class LikeRemover {
    fun remove(like: Like) {
        like.delete()
    }
}
