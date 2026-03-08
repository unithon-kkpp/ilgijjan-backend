package com.ilgijjan.unit.domain.user.domain

import com.ilgijjan.domain.auth.domain.OauthInfo
import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.user.domain.User
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

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
        // given
        val user = createUser()

        // when
        user.withdraw()

        // then
        assertThat(user.deletedAt).isNotNull()
    }

    @Test
    fun `탈퇴 시 provider_id가 WITHDRAWN_ 접두사로 변경된다`() {
        // given
        val originalProviderId = "4679645572"
        val user = createUser(providerId = originalProviderId)

        // when
        user.withdraw()

        // then
        assertThat(user.oauthInfo.providerId).isEqualTo("WITHDRAWN_$originalProviderId")
    }

    @Test
    fun `탈퇴 시 provider는 변경되지 않는다`() {
        // given
        val user = createUser()

        // when
        user.withdraw()

        // then
        assertThat(user.oauthInfo.provider).isEqualTo(OauthProvider.KAKAO)
    }
}
