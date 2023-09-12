package com.ajaxproject.telegrambot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TelegramBotApplication

fun main(vararg args: String) {
    runApplication<TelegramBotApplication>(*args)
}
