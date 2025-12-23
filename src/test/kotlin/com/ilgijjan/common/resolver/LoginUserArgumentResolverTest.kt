package com.ilgijjan.common.resolver

import com.ilgijjan.common.annotation.LoginUser
import com.ilgijjan.common.jwt.JwtTokenProvider
import com.ilgijjan.common.jwt.TokenType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootTest
@AutoConfigureMockMvc
@Import(LoginUserArgumentResolverTest.TestController::class)
class LoginUserArgumentResolverTest @Autowired constructor(
    private val mockMvc: MockMvc,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @RestController
    class TestController {
        @GetMapping("/test/auth")
        fun testAuth(@LoginUser userId: Long): Long {
            return userId
        }
    }

    @Test
    fun `토큰을 넣으면 @LoginUser가 UserId를 잘 가져온다`() {
        // given
        val userId = 12345L
        val token = jwtTokenProvider.createToken(userId, TokenType.ACCESS)

        // when & then
        mockMvc.perform(
            get("/test/auth")
                .header("Authorization", "Bearer $token")
        )
            .andExpect(status().isOk)
            .andExpect(content().string(userId.toString()))
            .andDo { print() }
    }

    @Test
    fun `토큰(헤더)이 없으면 401 에러가 터진다`() {
        // when & then
        mockMvc.perform(
            get("/test/auth")
        )
            .andExpect(status().isUnauthorized)
            .andDo { print() }
    }
}
