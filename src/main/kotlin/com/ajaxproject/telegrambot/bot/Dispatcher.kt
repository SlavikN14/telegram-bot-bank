package com.ajaxproject.telegrambot.bot

import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component

@Component
class Dispatcher(
    private val handlers: List<UserRequestHandler>,
) {

    fun dispatch(updateRequest: UpdateRequest): Boolean {
        return handlers.firstOrNull { it.isApplicable(updateRequest) }?.run {
            handle(updateRequest)
            true
        } ?: false
    }
}
