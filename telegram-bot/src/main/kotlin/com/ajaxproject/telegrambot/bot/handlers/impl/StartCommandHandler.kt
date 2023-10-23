package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.Commands.ENG_FILE
import com.ajaxproject.telegrambot.bot.enums.Commands.UKR_FILE
import com.ajaxproject.telegrambot.bot.enums.Commands.START
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_NUMBER
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.WELCOME_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component

@Component
class StartCommandHandler(
    private val telegramService: TelegramService
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, START.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = "Choose the language",
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("ENG", ENG_FILE.command),
                        inlineButton("UKR", UKR_FILE.command)
                    )
                )
            }
        )
    }
}

@Component
class LanguageButtonsHandler(
    private val textService: TextService,
    private val userSessionService: UserSessionService,
    private val telegramService: TelegramService,
) : UserRequestHandler {
    
    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(
            update = request.update,
            command = arrayOf(ENG_FILE.command, UKR_FILE.command)
        )
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        textService.pathResource = dispatchRequest.update.callbackQuery.data

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
