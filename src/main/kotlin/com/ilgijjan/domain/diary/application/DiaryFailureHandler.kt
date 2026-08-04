package com.ilgijjan.domain.diary.application

import com.ilgijjan.common.constants.WalletConstants
import com.ilgijjan.domain.wallet.application.UserWalletUpdater
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DiaryFailureHandler(
    private val diaryUpdater: DiaryUpdater,
    private val userWalletUpdater: UserWalletUpdater
) {
    @Transactional
    fun handle(diaryId: Long, userId: Long) {
        diaryUpdater.fail(diaryId)
        userWalletUpdater.charge(userId, WalletConstants.DIARY_CREATION_COST)
    }
}
