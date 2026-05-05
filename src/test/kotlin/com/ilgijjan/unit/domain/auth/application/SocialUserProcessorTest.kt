package com.ilgijjan.unit.domain.auth.application

import com.ilgijjan.domain.auth.application.OauthCommand
import com.ilgijjan.domain.auth.application.SocialUserProcessor
import com.ilgijjan.domain.auth.domain.OauthInfo
import com.ilgijjan.domain.auth.domain.OauthProvider
import com.ilgijjan.domain.user.application.UserCreator
import com.ilgijjan.domain.user.application.UserReader
import com.ilgijjan.domain.user.domain.User
import com.ilgijjan.integration.oauth.application.OauthClient
import com.ilgijjan.integration.oauth.application.OauthClients
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.time.LocalDateTime

class SocialUserProcessorTest {

    private lateinit var oauthClients: OauthClients
    private lateinit var userReader: UserReader
    private lateinit var userCreator: UserCreator
    private lateinit var oauthClient: OauthClient
    private lateinit var processor: SocialUserProcessor

    private val provider = OauthProvider.KAKAO
    private val providerId = "4679645572"
    private val command = OauthCommand(provider = provider, accessToken = "token")

    @BeforeEach
    fun setUp() {
        oauthClients = mock(OauthClients::class.java)
        userReader = mock(UserReader::class.java)
        userCreator = mock(UserCreator::class.java)
        oauthClient = mock(OauthClient::class.java)
        processor = SocialUserProcessor(oauthClients, userReader, userCreator)

        `when`(oauthClients.getClient(provider)).thenReturn(oauthClient)
        `when`(oauthClient.getProviderId(command)).thenReturn(providerId)
    }

    private fun createActiveUser() = User(
        oauthInfo = OauthInfo(provider = provider, providerId = providerId)
    )

    private fun createDeletedUser(deletedAt: LocalDateTime) = User(
        oauthInfo = OauthInfo(provider = provider, providerId = providerId)
    ).also { it.deletedAt = deletedAt }

    @Test
    fun `활성 유저가 있으면 그대로 반환한다`() {
        val activeUser = createActiveUser()
        `when`(userReader.findByProviderId(provider, providerId)).thenReturn(activeUser)

        val result = processor.getOrCreateUser(command)

        assertThat(result).isEqualTo(activeUser)
        verify(userReader, never()).findDeletedByProviderId(provider, providerId)
        verify(userCreator, never()).createSocialUser(provider, providerId)
    }

    @Test
    fun `7일 이내 탈퇴 유저는 복원하여 반환한다`() {
        val deletedUser = createDeletedUser(deletedAt = LocalDateTime.now().minusDays(3))
        `when`(userReader.findByProviderId(provider, providerId)).thenReturn(null)
        `when`(userReader.findDeletedByProviderId(provider, providerId)).thenReturn(deletedUser)

        val result = processor.getOrCreateUser(command)

        assertThat(result).isEqualTo(deletedUser)
        assertThat(result.deletedAt).isNull()
        verify(userCreator, never()).createSocialUser(provider, providerId)
    }

    @Test
    fun `7일 초과 탈퇴 유저는 provider_id를 초기화하고 신규 유저를 생성한다`() {
        val deletedUser = createDeletedUser(deletedAt = LocalDateTime.now().minusDays(8))
        val newUser = createActiveUser()
        `when`(userReader.findByProviderId(provider, providerId)).thenReturn(null)
        `when`(userReader.findDeletedByProviderId(provider, providerId)).thenReturn(deletedUser)
        `when`(userCreator.createSocialUser(provider, providerId)).thenReturn(newUser)

        val result = processor.getOrCreateUser(command)

        assertThat(deletedUser.oauthInfo.providerId).isNull()
        assertThat(result).isEqualTo(newUser)
    }

    @Test
    fun `탈퇴 이력이 없으면 신규 유저를 생성한다`() {
        val newUser = createActiveUser()
        `when`(userReader.findByProviderId(provider, providerId)).thenReturn(null)
        `when`(userReader.findDeletedByProviderId(provider, providerId)).thenReturn(null)
        `when`(userCreator.createSocialUser(provider, providerId)).thenReturn(newUser)

        val result = processor.getOrCreateUser(command)

        assertThat(result).isEqualTo(newUser)
        verify(userCreator).createSocialUser(provider, providerId)
    }
}
