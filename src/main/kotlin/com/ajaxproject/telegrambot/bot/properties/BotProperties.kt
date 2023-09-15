package com.ajaxproject.telegrambot.bot.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "bot")
data class BotProperties @ConstructorBinding constructor(
    val token: String,
    val username: String
)

@ConfigurationProperties(prefix = "text")
data class TextProperties @ConstructorBinding constructor(
    val path: String,
    val fileName: String
)
