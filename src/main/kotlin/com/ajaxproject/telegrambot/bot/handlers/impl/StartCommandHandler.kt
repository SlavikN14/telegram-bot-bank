package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.ConversationState
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.model.UserRequest
import com.ajaxproject.telegrambot.bot.model.UserSession
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.utils.Id
import com.ajaxproject.telegrambot.bot.utils.TextsUtils
import org.springframework.stereotype.Component

@Component
class StartCommandHandler(
    val telegramService: TelegramService,
    val text: TextsUtils,
    val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UserRequest): Boolean {
        return isCommand(request.update, START)
    }

    override fun handle(dispatchRequest: UserRequest) {
        telegramService.sendMessage(
            dispatchRequest.chatId,
            text.getText(Id.WELCOME)
        )
        val session: UserSession = dispatchRequest.userSession
        session.state = ConversationState.WAITING_FOR_TEXT
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }

    override val isGlobal: Boolean = true

    companion object {
        const val START = "/start"
    }
}
