package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.annotations.VeryPoliteCommand
import com.ajaxproject.telegrambot.bot.annotations.VeryPoliteCommandHandler
import com.ajaxproject.telegrambot.bot.enums.ConversationState
import com.ajaxproject.telegrambot.bot.enums.Currency
import com.ajaxproject.telegrambot.bot.enums.PropertyName
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.models.user.UserRequest
import com.ajaxproject.telegrambot.bot.models.user.UserSession
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import com.ajaxproject.telegrambot.bot.utils.TextService
import com.ajaxproject.telegrambot.bot.utils.isTextMessage
import org.springframework.stereotype.Component

@Component
@VeryPoliteCommand
class TextEnteredHandler(
    private val telegramService: TelegramService,
    private val userSessionService: UserSessionService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UserRequest): Boolean {
        return ConversationState.WAITING_FOR_TEXT == request.userSession.state &&
            request.update.isTextMessage()
    }

    @VeryPoliteCommandHandler
    override fun handle(dispatchRequest: UserRequest) {
        val textFromUser = dispatchRequest.update.message.text
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textFromUser + textService.readText(PropertyName.FUNCTIONS.name),
            replyKeyboard = KeyboardUtils.inlineKeyboard(
                KeyboardUtils.inlineRowKeyboard(
                    listOf(
                        KeyboardUtils.inlineButton("USD", Currency.USD.code.toString()),
                        KeyboardUtils.inlineButton("EUR", Currency.EUR.code.toString())
                    )
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
