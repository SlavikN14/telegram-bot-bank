package com.ajaxproject.telegrambot.bot.handlers

import com.ajaxproject.telegrambot.bot.model.UserRequest
import org.telegram.telegrambots.meta.api.objects.Update

interface UserRequestHandler {

    fun isApplicable(request: UserRequest): Boolean

    fun handle(dispatchRequest: UserRequest)

    val isGlobal: Boolean

    fun isCommand(update: Update, command: String): Boolean {
        return update.hasMessage() && update.message.isCommand() && update.message.text.equals(command) ||
            update.hasCallbackQuery() && update.callbackQuery.data == command
    }
}
