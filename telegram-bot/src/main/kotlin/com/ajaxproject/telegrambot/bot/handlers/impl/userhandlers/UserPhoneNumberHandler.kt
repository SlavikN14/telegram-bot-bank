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
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

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

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        val phoneNumber = dispatchRequest.update.message.text

        return Mono.just(phoneNumber)
            .filter { it.contains(REGEX_PHONE_NUMBER) }
            .switchIfEmpty {
                sendMessageWrongPhoneNumber(dispatchRequest, phoneNumber)
            }
            .flatMap { addUser(dispatchRequest, phoneNumber) }
    }

    private fun sendMessageWrongPhoneNumber(dispatchRequest: UpdateRequest, phoneNumber: String): Mono<String> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.textMap[dispatchRequest.updateSession.localization]?.get(WRONG_NUMBER_TEXT.name)
                .toString()
        )
            .doOnNext {
                log.error("Wrong phone number: {}", phoneNumber)
            }
            .then(Mono.empty())
    }

    private fun addUser(dispatchRequest: UpdateRequest, phoneNumber: String): Mono<Unit> {
        return userService.addUser(MongoUser(dispatchRequest.chatId, phoneNumber))
            .then(
                userSessionService.updateSession(
                    CONVERSATION_STARTED,
                    dispatchRequest.chatId,
                    dispatchRequest.updateSession.localization
                )
            )
            .then(
                menuCommandHandler.handle(dispatchRequest)
            )
    }

    companion object {
        val REGEX_PHONE_NUMBER =
            "^\\+\\d{12}\$".toRegex()
        private val log = LoggerFactory.getLogger(UserPhoneNumberHandler::class.java)
    }
}
