package com.ajaxproject.telegrambot.bot.sender

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions
@Component
class BankInfoBotSender : DefaultAbsSender(DefaultBotOptions()) {
    @Value("\${bot.token}")
    private val botToken: String = ""

    override fun getBotToken(): String = botToken
}
