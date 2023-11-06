package com.ajaxproject.telegrambot.handler.impl.financehandlers

import com.ajaxproject.telegrambot.enums.Commands
import com.ajaxproject.telegrambot.enums.ConversationState.WAITING_FOR_ADD_FINANCE
import com.ajaxproject.telegrambot.enums.TextPropertyName
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.TextService
import com.ajaxproject.telegrambot.service.UserSessionService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class AddFinancesButtonHandler(
    private val telegramService: TelegramMessageService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, Commands.ADD_FINANCE.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.getText(
                dispatchRequest.updateSession.localization,
                TextPropertyName.ADD_FINANCE_TEXT.name
            )
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
