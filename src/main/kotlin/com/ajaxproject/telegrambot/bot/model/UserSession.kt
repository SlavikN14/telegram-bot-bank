package com.ajaxproject.telegrambot.bot.model

import com.ajaxproject.telegrambot.bot.enums.ConversationState

data class UserSession(
    var state: ConversationState? = null,
    val chatId: Long? = null,
    var text: String? = null
)
