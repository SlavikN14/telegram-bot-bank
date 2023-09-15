package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.ConversationState
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.model.UserRequest
import com.ajaxproject.telegrambot.bot.model.UserSession
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.utils.Id
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import com.ajaxproject.telegrambot.bot.utils.TextsUtils
import com.ajaxproject.telegrambot.bot.utils.isTextMessage
import org.springframework.stereotype.Component

const val CURRENCY = "/currency"

@Component
class TextEnteredHandler(
    val telegramService: TelegramService,
    val text: TextsUtils,
    val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UserRequest): Boolean {
        return ConversationState.WAITING_FOR_TEXT == request.userSession.state &&
            request.update.isTextMessage()
    }

    override fun handle(dispatchRequest: UserRequest) {
        val textFromUser = dispatchRequest.update.message.text
        telegramService.sendMessage(
            dispatchRequest.chatId,
            textFromUser + text.getText(Id.FUNCTIONS),
            KeyboardUtils.inlineKeyboard(
                KeyboardUtils.inlineRowKeyboard(
                    listOf(KeyboardUtils.inlineButton("Get Currency", CURRENCY))
                )
            )
        )

        val session: UserSession = dispatchRequest.userSession.apply {
            text = textFromUser
            state = ConversationState.CONVERSATION_STARTED
        }
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }

    override val isGlobal: Boolean = false
}
