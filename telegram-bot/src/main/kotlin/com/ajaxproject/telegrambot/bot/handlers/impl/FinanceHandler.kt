package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.dto.toFinanceResponse
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_EXPENSES
import com.ajaxproject.telegrambot.bot.enums.Commands.ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_ADD_FINANCE
import com.ajaxproject.financemodelsapi.enums.Finance
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.ADD_FINANCE_TEXT
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.BACK_TO_MENU
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.FAILED_ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.SUCCESSFUL_ADD_FINANCE
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.financemodelsapi.models.MongoFinance
import com.ajaxproject.financemodelsapi.enums.Finance.INCOME
import com.ajaxproject.financemodelsapi.enums.Finance.EXPENSE
import com.ajaxproject.telegrambot.bot.service.FinanceRequestNatsService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.isTextMessage
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component
import java.util.*

@Component
class AddFinancesButtonHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, ADD_FINANCE.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(ADD_FINANCE_TEXT.name)
        )
        val session: UpdateSession = dispatchRequest.updateSession.apply {
            state = WAITING_FOR_ADD_FINANCE
        }
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }
}

@Component
class AddFinanceHandler(
    private val telegramService: TelegramService,
    private val financeRequestNatsService: FinanceRequestNatsService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return WAITING_FOR_ADD_FINANCE == request.updateSession.state &&
                request.update.isTextMessage()
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        val financeData = dispatchRequest.update.message.text
        val chatId = dispatchRequest.chatId
        val financeType = checkDataIncomeOrExpense(financeData)

        if (!financeData.matches(Regex("[+-]\\d+\\s+\\w+"))) {
            telegramService.sendMessage(chatId, textService.readText(FAILED_ADD_FINANCE.name))
            return
        }

        financeRequestNatsService.requestToCreateFinance(
            MongoFinance(
                userId = dispatchRequest.chatId,
                financeType = financeType,
                amount = financeData.substring(1).split(" ")[0].toDouble(),
                description = financeData.split(" ")[1],
                date = Date()
            )
        )

        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(SUCCESSFUL_ADD_FINANCE.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("Add finance again", ADD_FINANCE.command),
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

    private fun checkDataIncomeOrExpense(financeData: String): Finance {
        return when {
            financeData.contains("+") -> INCOME
            financeData.contains("-") -> EXPENSE
            else -> throw IllegalArgumentException("Unknown finance type")
        }
    }
}

@Component
class GetFinancesHandler(
    private val telegramService: TelegramService,
    private val financeRequestNatsService: FinanceRequestNatsService,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_INCOMES.command, GET_EXPENSES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        financeRequestNatsService.requestToGetAllFinancesByUserId(
            dispatchRequest.chatId,
            dispatchRequest.update.callbackQuery.data.checkCommandIncomeOrExpense()
        )
            .map { it.toFinanceResponse() }
            .forEach {
                telegramService.sendMessage(
                    chatId = dispatchRequest.chatId,
                    text = it.toString()
                )
            }
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

    private fun String.checkCommandIncomeOrExpense(): Finance {
        return when (this) {
            GET_INCOMES.command -> INCOME
            GET_EXPENSES.command -> EXPENSE
            else -> throw IllegalArgumentException("Unknown finance type")
        }
    }

}

