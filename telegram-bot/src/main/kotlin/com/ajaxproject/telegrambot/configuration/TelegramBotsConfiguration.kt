package com.ajaxproject.telegrambot.configuration

import com.ajaxproject.telegrambot.service.telegram.TelegramUpdateDispatcherService
import com.ajaxproject.telegrambot.BotProperties
import com.ajaxproject.telegrambot.MonobankProperties
import com.ajaxproject.telegrambot.RedisProperties
import io.nats.client.Connection
import io.nats.client.Nats
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
@EnableConfigurationProperties(BotProperties::class, MonobankProperties::class)
class TelegramBotsConfiguration {

    @Bean
    fun telegramBotsApi(property: TelegramUpdateDispatcherService): TelegramBotsApi =
        TelegramBotsApi(DefaultBotSession::class.java).apply {
            registerBot(property)
        }

    @Bean
    fun webClient(builder: WebClient.Builder, monobankProperties: MonobankProperties): WebClient = builder
        .baseUrl(monobankProperties.url)
        .build()

    @Bean
    fun setNatsConnection(@Value("\${nats.url}") natsUrl: String): Connection = Nats.connect(natsUrl)
}
