package com.ajaxproject.telegrambot.bot.handlers

import com.ajaxproject.telegrambot.bot.models.user.UserRequest
import org.telegram.telegrambots.meta.api.objects.Update

interface UserRequestHandler {

    fun isApplicable(request: UserRequest): Boolean

    fun handle(dispatchRequest: UserRequest)

    val isGlobal: Boolean

    fun isCommand(update: Update, vararg command: String): Boolean {
        return update.hasMessage() && update.message.isCommand() && command.contains(update.message.text) ||
                update.hasCallbackQuery() && command.contains(update.callbackQuery.data)
    }
}
