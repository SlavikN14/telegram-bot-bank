package com.ajaxproject.telegrambot.bot.service.updatemodels

import com.ajaxproject.telegrambot.bot.enums.ConversationState
import com.ajaxproject.telegrambot.bot.enums.Localization

data class UpdateSession(
    var state: ConversationState? = null,
    val chatId: Long? = null,
    var localization: Localization = Localization.DEFAULT_LOCALIZATION
)
