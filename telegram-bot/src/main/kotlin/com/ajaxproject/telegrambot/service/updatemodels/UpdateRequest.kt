package com.ajaxproject.telegrambot.service.updatemodels

import org.telegram.telegrambots.meta.api.objects.Update

data class UpdateRequest(
    val update: Update,
    val chatId: Long,
    val updateSession: UpdateSession
)
