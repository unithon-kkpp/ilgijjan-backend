package com.ilgijjan.domain.user.application

import com.ilgijjan.domain.user.domain.Character
import com.ilgijjan.domain.user.presentation.ReadMeResponse
import com.ilgijjan.domain.user.presentation.ReadNoteResponse
import com.ilgijjan.domain.wallet.application.UserWalletReader
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userReader: UserReader,
    private val userUpdater: UserUpdater,
    private val userWalletReader: UserWalletReader
) {
    fun getMe(userId: Long): ReadMeResponse {
        val user = userReader.getUserById(userId)
        val wallet = userWalletReader.getByUserId(userId)
        return ReadMeResponse.from(user, wallet.noteCount)
    }

    fun getMyNoteCount(userId: Long): ReadNoteResponse {
        val wallet = userWalletReader.getByUserId(userId)
        return ReadNoteResponse(wallet.noteCount)
    }

    @Transactional
    fun updateName(userId: Long, newName: String) {
        userUpdater.updateName(userId, newName)
    }

    @Transactional
    fun updateCharacter(userId: Long, character: Character) {
        userUpdater.updateCharacter(userId, character)
    }

    @Transactional
    fun updateNotification(userId: Long, isEnabled: Boolean) {
        userUpdater.updateNotification(userId, isEnabled)
    }
}
