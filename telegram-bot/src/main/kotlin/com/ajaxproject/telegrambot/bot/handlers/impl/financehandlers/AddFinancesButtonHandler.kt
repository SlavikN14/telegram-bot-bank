package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.telegrambot.bot.enums.Commands
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.textIsNotUploaded
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AddFinancesButtonHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, Commands.ADD_FINANCE.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.textMap[dispatchRequest.updateSession.localization]
                ?.get(TextPropertyName.ADD_FINANCE_TEXT.name).textIsNotUploaded()
        )
            .then(
                userSessionService.updateSession(
                    WAITING_FOR_ADD_FINANCE,
                    dispatchRequest.chatId,
                    dispatchRequest.updateSession.localization
                )
            )
            .thenReturn(Unit)
    }
}
