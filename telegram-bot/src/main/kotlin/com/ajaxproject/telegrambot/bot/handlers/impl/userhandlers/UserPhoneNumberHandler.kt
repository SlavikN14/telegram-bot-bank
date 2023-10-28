package com.ajaxproject.telegrambot.bot.handlers.impl.userhandlers

import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_NUMBER
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.WRONG_NUMBER_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.handlers.impl.MenuCommandHandler
import com.ajaxproject.telegrambot.bot.models.MongoUser
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.isTextMessage
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import org.springframework.stereotype.Component

@Component
class UserPhoneNumberHandler(
    private val telegramService: TelegramService,
    private val userSessionService: UserSessionService,
    private val textService: TextService,
    private val userService: UserService,
    private val menuCommandHandler: MenuCommandHandler,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return WAITING_FOR_NUMBER == request.updateSession.state &&
                request.update.isTextMessage()
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        val phoneNumber = dispatchRequest.update.message.text
        val chatId = dispatchRequest.chatId

        if (!phoneNumber.contains(REGEX_PHONE_NUMBER)) {
            telegramService.sendMessage(chatId, textService.readText(WRONG_NUMBER_TEXT.name))
            return
        }

        userService.addUser(MongoUser(chatId, phoneNumber))
            .doOnNext {
                menuCommandHandler.handle(dispatchRequest)
                val session: UpdateSession = dispatchRequest.updateSession.apply {
                    state = CONVERSATION_STARTED
                }
                userSessionService.saveSession(dispatchRequest.chatId, session)
            }
            .subscribe()
    }

    companion object {
        val REGEX_PHONE_NUMBER =
            "^\\+\\d{12}\$".toRegex()
    }
}
