package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.telegrambot.bot.dto.toFinanceResponse
import com.ajaxproject.telegrambot.bot.enums.Buttons.BACK_TO_MENU_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_EXPENSES
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.BACK_TO_MENU
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.FinanceRequestNatsService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class GetFinancesModelsHandler(
    private val telegramService: TelegramService,
    private val financeRequestNatsService: FinanceRequestNatsService,
    private val userSessionService: UserSessionService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_INCOMES.command, GET_EXPENSES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        financeRequestNatsService.requestToGetAllFinancesByUserId(
            dispatchRequest.chatId,
            dispatchRequest.update.callbackQuery.data.checkCommandIncomeOrExpense()
        )
            .flatMapMany { list ->
                Flux.fromIterable(list)
                    .map { it.toFinanceResponse() }
            }
            .flatMap { financeResponse ->
                Mono.fromSupplier {
                    telegramService.sendMessage(
                        chatId = dispatchRequest.chatId,
                        text = financeResponse.toString()
                    )
                }.subscribeOn(Schedulers.boundedElastic())
            }
            .flatMap {
                Mono.fromSupplier {
                    dispatchRequest.updateSession.apply { state = CONVERSATION_STARTED }
                }
            }
            .flatMap { session ->
                userSessionService.saveSession(dispatchRequest.chatId, session)
                Mono.just(session)
            }
            .then(returnToMainMenu(dispatchRequest))
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun returnToMainMenu(dispatchRequest: UpdateRequest): Mono<Message> {
        return Mono.fromSupplier {
            telegramService.sendMessage(
                chatId = dispatchRequest.chatId,
                text = textService.readText(BACK_TO_MENU.name),
                replyKeyboard = KeyboardUtils.run {
                    inlineKeyboard(
                        inlineRowKeyboard(
                            inlineButton(
                                textService.readText(BACK_TO_MENU_BUTTON.name), MENU.command
                            )
                        )
                    )
                }
            )
        }
    }

    private fun String.checkCommandIncomeOrExpense(): Finance {
        return when (this) {
            GET_INCOMES.command -> Finance.INCOME
            GET_EXPENSES.command -> Finance.EXPENSE
            else -> throw IllegalArgumentException("Unknown finance type")
        }
    }
}

