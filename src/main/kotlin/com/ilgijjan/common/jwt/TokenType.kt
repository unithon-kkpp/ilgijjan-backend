package com.ilgijjan.common.jwt

enum class TokenType(val lifeTime: Long) {
    ACCESS(30 * 60 * 1000L),
    REFRESH(14 * 24 * 60 * 60 * 1000L),
    TEST(30 * 24 * 60 * 60 * 1000L)
}
