package com.ajaxproject.telegrambot.bot.configuration

import com.ajaxproject.telegrambot.bot.BankInfoBot
import com.ajaxproject.telegrambot.bot.properties.BotProperties
import com.ajaxproject.telegrambot.bot.properties.MonobankProperties
import com.ajaxproject.telegrambot.bot.properties.TextProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
@EnableConfigurationProperties(BotProperties::class, TextProperties::class, MonobankProperties::class)
class TelegramBotsConfiguration {
    @Bean
    fun telegramBotsApi(property: BankInfoBot): TelegramBotsApi =
        TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(property)
        }
}
