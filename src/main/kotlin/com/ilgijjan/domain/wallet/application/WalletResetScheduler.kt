package com.ilgijjan.domain.wallet.application

import com.ilgijjan.domain.wallet.infrastructure.UserWalletRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class WalletResetScheduler(
    private val userWalletRepository: UserWalletRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional
    fun resetDailyNotes() {
        userWalletRepository.resetAllNoteCount()
        log.info("[WalletResetScheduler] 전체 유저 음표 초기화 완료")
    }
}
