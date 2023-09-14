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
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton

@Component
class TextEnteredHandler(
    val telegramService: TelegramService,
    val text: TextsUtils,
    val userSessionService: UserSessionService,
) : UserRequestHandler() {
    override fun isApplicable(request: UserRequest): Boolean {
        return (isTextMessage(request.update)) &&
            (ConversationState.WAITING_FOR_TEXT == request.userSession.state)
    }

    override fun handle(dispatchRequest: UserRequest) {
        val textFromUser = dispatchRequest.update.message.text
        telegramService.sendMessage(
            dispatchRequest.chatId,
            textFromUser + text.getText(Id.FUNCTIONS),
            KeyboardUtils.replyKeyboard(
                KeyboardUtils.rowReplyKeyboard(KeyboardButton("Currency"))
            )
        )

        val session: UserSession = dispatchRequest.userSession
        session.text = textFromUser
        session.state = ConversationState.CONVERSATION_STARTED
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }

    override fun isGlobal(): Boolean = false
}
