package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.BackToMainMenu
import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.BackToMainMenuCommand
import com.ajaxproject.telegrambot.bot.enums.Buttons.CURRENT_BALANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_CURRENT_BALANCE
import com.ajaxproject.telegrambot.bot.enums.ConversationState
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.FinanceRequestNatsService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
@BackToMainMenu
class CurrentBalanceHandler(
    private val telegramService: TelegramService,
    private val financeRequestNatsService: FinanceRequestNatsService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_CURRENT_BALANCE.command)
    }

    @BackToMainMenuCommand
    override fun handle(dispatchRequest: UpdateRequest) {
        financeRequestNatsService.requestToGetCurrentBalance(dispatchRequest.chatId)
            .flatMap { balance ->
                Mono.fromSupplier {
                    telegramService.sendMessage(
                        chatId = dispatchRequest.chatId,
                        text = "${textService.readText(CURRENT_BALANCE.name)} $balance",
                    )
                }.subscribeOn(Schedulers.boundedElastic())
            }
            .flatMap {
                Mono.fromSupplier {
                    dispatchRequest.updateSession.apply { state = ConversationState.CONVERSATION_STARTED }
                }
            }
            .flatMap { session ->
                userSessionService.saveSession(dispatchRequest.chatId, session)
                Mono.just(session)
            }
            .subscribe()
    }
}
