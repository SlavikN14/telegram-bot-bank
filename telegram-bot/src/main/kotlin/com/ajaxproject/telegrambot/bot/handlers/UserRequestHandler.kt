package com.ajaxproject.telegrambot.bot.handlers

import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.telegram.telegrambots.meta.api.objects.Update
import reactor.core.publisher.Mono

interface UserRequestHandler {

    fun isApplicable(request: UpdateRequest): Boolean

    fun handle(dispatchRequest: UpdateRequest): Mono<Unit>

    fun isCommand(update: Update, vararg command: String): Boolean {
        return update.hasMessage() && update.message.isCommand() && command.contains(update.message.text) ||
            update.hasCallbackQuery() && command.contains(update.callbackQuery.data)
    }
}
