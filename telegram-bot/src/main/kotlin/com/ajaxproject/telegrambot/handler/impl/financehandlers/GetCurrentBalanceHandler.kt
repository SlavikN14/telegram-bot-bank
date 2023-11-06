package com.ajaxproject.telegrambot.handler.impl.financehandlers

import com.ajaxproject.telegrambot.enums.Buttons.CURRENT_BALANCE_BUTTON
import com.ajaxproject.telegrambot.enums.Commands.GET_CURRENT_BALANCE
import com.ajaxproject.telegrambot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.client.FinanceClient
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.TextService
import com.ajaxproject.telegrambot.service.UserSessionService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class GetCurrentBalanceHandler(
    private val telegramService: TelegramMessageService,
    private val financeClient: FinanceClient,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_CURRENT_BALANCE.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        val currentBalance = textService.getText(
            dispatchRequest.updateSession.localization,
            CURRENT_BALANCE_BUTTON.name
        )
        return financeClient.getCurrentBalance(dispatchRequest.chatId)
            .flatMap { balance ->
                telegramService.sendMessage(
                    chatId = dispatchRequest.chatId,
                    text = "$currentBalance $balance",
                )
            }
            .flatMap {
                userSessionService.updateSession(
                    CONVERSATION_STARTED,
                    dispatchRequest.chatId,
                    dispatchRequest.updateSession.localization
                )
            }
            .thenReturn(Unit)
    }
}
