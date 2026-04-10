package com.ilgijjan.domain.admin.presentation

import com.ilgijjan.domain.user.application.UserReader
import com.ilgijjan.domain.wallet.application.UserWalletReader
import com.ilgijjan.domain.wallet.application.UserWalletUpdater
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Controller
@RequestMapping("/admin")
class AdminController(
    private val userReader: UserReader,
    private val userWalletUpdater: UserWalletUpdater,
    private val userWalletReader: UserWalletReader,
) {

    @GetMapping
    fun index(model: Model): String = "admin/index"

    @GetMapping("/login-error")
    fun loginError(): String = "admin/login-error"

    @GetMapping("/search")
    fun search(@RequestParam nickname: String, model: Model): String {
        val user = userReader.findByName(nickname)
        if (user == null) {
            model.addAttribute("searchError", "닉네임 '$nickname'을 찾을 수 없습니다.")
        } else {
            val wallet = userWalletReader.getByUserId(user.id!!)
            model.addAttribute("foundNickname", nickname)
            model.addAttribute("currentNoteCount", wallet.noteCount)
        }
        model.addAttribute("searchedNickname", nickname)
        return "admin/index"
    }

    @PostMapping("/charge")
    fun charge(
        @RequestParam nickname: String,
        @RequestParam amount: Int,
        redirectAttributes: RedirectAttributes,
    ): String {
        val user = userReader.findByName(nickname)
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "닉네임 '$nickname'을 찾을 수 없습니다.")
            return "redirect:/admin"
        }

        userWalletUpdater.charge(user.id!!, amount)
        val wallet = userWalletReader.getByUserId(user.id)
        redirectAttributes.addFlashAttribute("success", "${nickname}님에게 ${amount}개 충전 완료. 현재 보유: ${wallet.noteCount}개")
        val encodedNickname = URLEncoder.encode(nickname, StandardCharsets.UTF_8)
        return "redirect:/admin/search?nickname=$encodedNickname"
    }
}
