package com.ajaxproject.telegrambot.bot

import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.model.UserRequest
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class Dispatcher(
    private val handlers: List<UserRequestHandler>,
) {
    @PostConstruct
    fun setupHandlers() {
        handlers.sortedWith(Comparator.comparing(UserRequestHandler::isGlobal).reversed())
            .toList()
    }

    fun dispatch(userRequest: UserRequest): Boolean {
        return handlers.firstOrNull { it.isApplicable(userRequest) }?.run {
            handle(userRequest)
            true
        } ?: false
    }
}
