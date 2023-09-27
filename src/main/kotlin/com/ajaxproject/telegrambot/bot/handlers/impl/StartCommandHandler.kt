package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.annotations.VeryPoliteCommand
import com.ajaxproject.telegrambot.bot.annotations.VeryPoliteCommandHandler
import com.ajaxproject.telegrambot.bot.enums.ConversationState
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.models.user.UserRequest
import com.ajaxproject.telegrambot.bot.models.user.UserSession
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.utils.Id
import com.ajaxproject.telegrambot.bot.utils.TextsUtils
import org.springframework.stereotype.Component

@Component
@VeryPoliteCommand
class StartCommandHandler(
    val telegramService: TelegramService,
    val text: TextsUtils,
    val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UserRequest): Boolean {
        return isCommand(request.update, START)
    }

    @VeryPoliteCommandHandler
    override fun handle(dispatchRequest: UserRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = text.getText(Id.WELCOME)
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
