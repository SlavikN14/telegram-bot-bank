package com.ajaxproject.telegrambot.bot.handlers

import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.telegram.telegrambots.meta.api.objects.Update

interface UserRequestHandler {

    fun isApplicable(request: UpdateRequest): Boolean

    fun handle(dispatchRequest: UpdateRequest)

    fun isCommand(update: Update, vararg command: String): Boolean {
        return update.hasMessage() && update.message.isCommand() && command.contains(update.message.text) ||
            update.hasCallbackQuery() && command.contains(update.callbackQuery.data)
    }
}
