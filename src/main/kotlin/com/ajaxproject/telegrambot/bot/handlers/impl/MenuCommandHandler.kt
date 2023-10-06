package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.Commands.ADD_EXPENSE
import com.ajaxproject.telegrambot.bot.enums.Commands.ADD_INCOME
import com.ajaxproject.telegrambot.bot.enums.Commands.CURRENCY
import com.ajaxproject.telegrambot.bot.enums.Commands.EXPENSES
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_CURRENT_BALANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_EXPENSE
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.bot.enums.Commands.INCOMES
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.Currency.EUR
import com.ajaxproject.telegrambot.bot.enums.Currency.USD
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.BACK_TO_MENU
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.CURRENCY_TEXT
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.FINANCE_MANAGE_TEXT
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.MENU_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.FinanceService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component

@Component
class MenuCommandHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, MENU.command) && CONVERSATION_STARTED == request.updateSession.state
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(MENU_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(inlineButton("Get Currency Rate", CURRENCY.command)),
                    inlineRowKeyboard(inlineButton("Manage Expenses", EXPENSES.command)),
                    inlineRowKeyboard(inlineButton("Manage Incomes", INCOMES.command)),
                    inlineRowKeyboard(inlineButton("Get Current Balance", GET_CURRENT_BALANCE.command))
                )
            }
        )
    }
}

@Component
class CurrencyButtonsHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, CURRENCY.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(CURRENCY_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("Get USD rate", USD.code.toString()),
                        inlineButton("Get EUR rate", EUR.code.toString())
                    )
                )
            }
        )
    }
}

@Component
class IncomesButtonsHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, INCOMES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(FINANCE_MANAGE_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("Add incomes", ADD_INCOME.command),
                        inlineButton("Get all incomes", GET_INCOMES.command)
                    )
                )
            }
        )
    }
}

@Component
class ExpensesButtonsHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, EXPENSES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(FINANCE_MANAGE_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("Add expenses", ADD_EXPENSE.command),
                        inlineButton("Get all expenses", GET_EXPENSE.command)
                    )
                )
            }
        )
    }
}

@Component
class CurrentBalanceHandler(
    private val telegramService: TelegramService,
    private val financeService: FinanceService,
    private val textService: TextService,
    private val userSessionService: UserSessionService
) : UserRequestHandler {
    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_CURRENT_BALANCE.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = "Your current balance is ${financeService.getCurrencyBalance(dispatchRequest.chatId)}"
        )
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(BACK_TO_MENU.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("Return to menu", MENU.command)
                    )
                )
            }
        )
        val session: UpdateSession = dispatchRequest.updateSession.apply {
            state = CONVERSATION_STARTED
        }
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }
}
