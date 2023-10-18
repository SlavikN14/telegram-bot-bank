package com.ajaxproject.telegrambot.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TelegramBotApplication

fun main(vararg args: String) {
    runApplication<TelegramBotApplication>(*args)
}
