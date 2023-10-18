package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_EXPENSES
import com.ajaxproject.telegrambot.bot.enums.Commands.ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_CURRENT_BALANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.MANAGE_FINANCES
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.Commands.CURRENCY
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.Currency.EUR
import com.ajaxproject.telegrambot.bot.enums.Currency.USD
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.MENU_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.CURRENCY_TEXT
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.FINANCE_MANAGE_TEXT
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.BACK_TO_MENU
import com.ajaxproject.telegrambot.bot.service.FinanceRequestNatsService
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
                    inlineRowKeyboard(inlineButton("Manage Finances", MANAGE_FINANCES.command)),
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
class FinanceButtonsHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, MANAGE_FINANCES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(FINANCE_MANAGE_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(inlineButton("Add finance", ADD_FINANCE.command)),
                    inlineRowKeyboard(inlineButton("Get all incomes", GET_INCOMES.command)),
                    inlineRowKeyboard(inlineButton("Get all expenses", GET_EXPENSES.command))
                )
            }
        )
    }
}

@Component
class CurrentBalanceHandler(
    private val telegramService: TelegramService,
    private val financeRequestNatsService: FinanceRequestNatsService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {
    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_CURRENT_BALANCE.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        val currentBalanceRequest = financeRequestNatsService.requestToGetCurrentBalance(dispatchRequest.chatId)
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = "Your current balance is $currentBalanceRequest",
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
