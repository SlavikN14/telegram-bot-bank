package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.BackToMainMenu
import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.BackToMainMenuCommand
import com.ajaxproject.telegrambot.bot.dto.toFinanceResponse
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_EXPENSES
import com.ajaxproject.telegrambot.bot.enums.ConversationState
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.FinanceRequestNatsService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import org.springframework.stereotype.Component

@Component
@BackToMainMenu
class GetFinancesModelsHandler(
    private val telegramService: TelegramService,
    private val financeRequestNatsService: FinanceRequestNatsService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_INCOMES.command, GET_EXPENSES.command)
    }

    @BackToMainMenuCommand
    override fun handle(dispatchRequest: UpdateRequest) {
        financeRequestNatsService.requestToGetAllFinancesByUserId(
            dispatchRequest.chatId,
            dispatchRequest.update.callbackQuery.data.checkCommandIncomeOrExpense()
        )
            .map { list -> list.map { it.toFinanceResponse() } }
            .doOnNext { financeResponses ->
                financeResponses.forEach {
                    telegramService.sendMessage(
                        chatId = dispatchRequest.chatId,
                        text = it.toString()
                    )
                    val session: UpdateSession = dispatchRequest.updateSession.apply {
                        state = ConversationState.CONVERSATION_STARTED
                    }
                    userSessionService.saveSession(dispatchRequest.chatId, session)
                }
            }.subscribe()
    }

    private fun String.checkCommandIncomeOrExpense(): Finance {
        return when (this) {
            GET_INCOMES.command -> Finance.INCOME
            GET_EXPENSES.command -> Finance.EXPENSE
            else -> throw IllegalArgumentException("Unknown finance type")
        }
    }
}

