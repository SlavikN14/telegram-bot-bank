package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.VeryPoliteCommand
import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.VeryPoliteCommandHandler
import com.ajaxproject.telegrambot.bot.enums.Commands.START
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_NUMBER
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.WELCOME_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import org.springframework.stereotype.Component

@Component
@VeryPoliteCommand
class StartCommandHandler(
    private val telegramService: TelegramService,
    private val userSessionService: UserSessionService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, START.command)
    }

    @VeryPoliteCommandHandler
    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(WELCOME_TEXT.name)
        )
        val session: UpdateSession = dispatchRequest.updateSession.apply {
            state = WAITING_FOR_NUMBER
        }
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }
}
