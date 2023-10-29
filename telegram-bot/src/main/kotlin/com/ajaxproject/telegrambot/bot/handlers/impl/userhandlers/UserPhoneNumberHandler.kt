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
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
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

    override fun handle(dispatchRequest: UpdateRequest) {
        val phoneNumber = dispatchRequest.update.message.text
        val chatId = dispatchRequest.chatId

        Mono.just(phoneNumber)
            .filter { it.contains(REGEX_PHONE_NUMBER) }
            .switchIfEmpty {
                Mono.fromSupplier { telegramService.sendMessage(chatId, textService.readText(WRONG_NUMBER_TEXT.name)) }
                    .subscribeOn(Schedulers.boundedElastic())
                    .then(Mono.empty())
            }
            .flatMap { userService.addUser(MongoUser(chatId, it)) }
            .flatMap {
                Mono.fromSupplier {
                    dispatchRequest.updateSession.apply { state = CONVERSATION_STARTED }
                }
            }
            .flatMap { session ->
                userSessionService.saveSession(dispatchRequest.chatId, session)
                Mono.just(session)
            }
            .flatMap {
                Mono.fromSupplier {
                    menuCommandHandler.handle(dispatchRequest)
                }.subscribeOn(Schedulers.boundedElastic())
            }
            .subscribe()
    }

    companion object {
        val REGEX_PHONE_NUMBER =
            "^\\+\\d{12}\$".toRegex()
    }
}
