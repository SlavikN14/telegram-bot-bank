package com.ajaxproject.telegrambot.bot.sender

import com.ajaxproject.telegrambot.bot.properties.BotProperties
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions

@Component
class BankInfoBotSender(
    val properties: BotProperties,
) : DefaultAbsSender(DefaultBotOptions()) {

    override fun getBotToken(): String = properties.token
}
