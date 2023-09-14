package com.ajaxproject.telegrambot.bot.configuration

import com.ajaxproject.telegrambot.bot.BankInfoBot
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
class TelegramBotsConfiguration {
    @Bean
    fun telegramBotsApi(property: BankInfoBot): TelegramBotsApi =
        TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(property)
        }
}
