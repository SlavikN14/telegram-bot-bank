package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.sender.BankInfoBotSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException

@Component
class TelegramService(
    val botSender: BankInfoBotSender,
) {
    fun sendMessage(chatId: Long, text: String) {
        sendMessage(chatId, text, null)
    }

    fun sendMessage(chatId: Long, text: String, replyKeyboard: ReplyKeyboardMarkup?) {
        val sendMessage = SendMessage.builder()
            .text(text)
            .chatId(chatId)
            .replyMarkup(replyKeyboard)
            .build()
        execute(sendMessage)
    }

    private fun execute(botApiMethod: BotApiMethodMessage) {
        try {
            botSender.execute(botApiMethod)
        } catch (e: TelegramApiRequestException) {
            log.error("Exception: ", e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TelegramService::class.java)
    }
}
