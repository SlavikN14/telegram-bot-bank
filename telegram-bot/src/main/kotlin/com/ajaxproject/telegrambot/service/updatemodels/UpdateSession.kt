package com.ajaxproject.telegrambot.service.updatemodels

import com.ajaxproject.telegrambot.enums.ConversationState
import com.ajaxproject.telegrambot.enums.Localization

data class UpdateSession(
    var state: ConversationState? = null,
    val chatId: Long? = null,
    var localization: Localization = Localization.DEFAULT_LOCALIZATION
)
