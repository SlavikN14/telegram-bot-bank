package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.Commands.START
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_NUMBER
import com.ajaxproject.telegrambot.bot.enums.Localization
import com.ajaxproject.telegrambot.bot.enums.Localization.ENG_LOCALIZATION
import com.ajaxproject.telegrambot.bot.enums.Localization.UKR_LOCALIZATION
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.WELCOME_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class StartCommandHandler(
    private val telegramService: TelegramService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, START.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = "Choose the language",
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardInOneRow(
                    inlineButton("ENG", ENG_LOCALIZATION.name),
                    inlineButton("UKR", UKR_LOCALIZATION.name)
                )
            }
        ).thenReturn(Unit)
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
            command = arrayOf(ENG_LOCALIZATION.name, UKR_LOCALIZATION.name)
        )
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return Mono.just(Localization.valueOf(dispatchRequest.update.callbackQuery.data))
            .flatMap {
                userSessionService.updateSession(WAITING_FOR_NUMBER, dispatchRequest.chatId, it)
            }
            .flatMap { session ->
                telegramService.sendMessage(
                    chatId = dispatchRequest.chatId,
                    text = textService.textMap[session.localization]?.get(WELCOME_TEXT.name)
                        .toString(),
                )
            }
            .thenReturn(Unit)
    }
}
