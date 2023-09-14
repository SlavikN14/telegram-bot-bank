package com.ajaxproject.telegrambot.bot.handlers

import com.ajaxproject.telegrambot.bot.model.UserRequest
import org.telegram.telegrambots.meta.api.objects.Update

abstract class UserRequestHandler {
    abstract fun isApplicable(request: UserRequest): Boolean
    abstract fun handle(dispatchRequest: UserRequest)
    abstract fun isGlobal(): Boolean
    fun isCommand(update: Update, command: String): Boolean {
        return update.hasMessage() && update.message.isCommand() &&
            update.message.text.equals(command)
    }

    fun isTextMessage(update: Update): Boolean {
        return update.hasMessage() && update.message.hasText()
    }

    fun isTextMessage(update: Update, text: String): Boolean {
        return update.hasMessage() && update.message.hasText() && update.message.text.equals(text)
    }
}
