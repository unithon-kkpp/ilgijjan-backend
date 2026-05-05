package com.ilgijjan.unit.domain.user.domain

import com.ilgijjan.domain.auth.domain.OauthInfo
import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class UserTest {

    private fun createUser(providerId: String = "4679645572"): User {
        return User(
            oauthInfo = OauthInfo(
                provider = OauthProvider.KAKAO,
                providerId = providerId
            )
        )
    }

    @Test
    fun `탈퇴 시 deleted_at이 설정된다`() {
        val user = createUser()

        user.withdraw()

        assertThat(user.deletedAt).isNotNull()
    }

    @Test
    fun `탈퇴 시 provider_id가 유지된다`() {
        val user = createUser(providerId = "4679645572")

        user.withdraw()

        assertThat(user.oauthInfo.providerId).isEqualTo("4679645572")
    }

    @Test
    fun `탈퇴 시 provider는 변경되지 않는다`() {
        val user = createUser()

        user.withdraw()

        assertThat(user.oauthInfo.provider).isEqualTo(OauthProvider.KAKAO)
    }

    @Test
    fun `복원 시 deleted_at이 null로 초기화된다`() {
        val user = createUser()
        user.withdraw()

        user.restore()

        assertThat(user.deletedAt).isNull()
    }

    @Test
    fun `provider_id 초기화 시 null로 변경된다`() {
        val user = createUser()

        user.clearProviderId()

        assertThat(user.oauthInfo.providerId).isNull()
    }

    @Test
    fun `탈퇴 후 7일 이내면 재가입 가능 기간이다`() {
        val user = createUser()
        user.withdraw()

        assertThat(user.isWithinRejoinWindow).isTrue()
    }

    @Test
    fun `탈퇴 후 7일 초과면 재가입 가능 기간이 아니다`() {
        val user = createUser()
        user.deletedAt = LocalDateTime.now().minusDays(8)

        assertThat(user.isWithinRejoinWindow).isFalse()
    }

    @Test
    fun `탈퇴하지 않은 유저는 재가입 가능 기간이 아니다`() {
        val user = createUser()

        assertThat(user.isWithinRejoinWindow).isFalse()
    }
}
