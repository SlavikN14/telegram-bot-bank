package com.ajaxproject.telegrambot

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.bind.ConstructorBinding

@ConfigurationProperties(prefix = "bot")
data class BotProperties @ConstructorBinding constructor(
    val token: String,
    val username: String
)

@ConfigurationProperties(prefix = "mono")
data class MonobankProperties @ConstructorBinding constructor(
    val url: String
)

@ConfigurationProperties(prefix = "redis-properties")
data class RedisProperties @ConstructorBinding constructor(
    val ttlMinutes: Int,
    val prefixKey: String
)
