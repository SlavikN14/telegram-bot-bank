package com.ajaxproject.telegrambot.bot.service.updatemodels

import com.ajaxproject.telegrambot.bot.enums.ConversationState

data class UpdateSession(
    var state: ConversationState? = null,
    val chatId: Long? = null,
    var text: String? = null
)
