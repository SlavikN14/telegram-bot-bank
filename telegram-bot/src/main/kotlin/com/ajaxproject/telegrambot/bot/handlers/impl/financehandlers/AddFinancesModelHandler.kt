package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.enums.Finance.EXPENSE
import com.ajaxproject.financemodels.enums.Finance.INCOME
import com.ajaxproject.financemodels.models.MongoFinance
import com.ajaxproject.telegrambot.bot.enums.Buttons.ADD_FINANCE_AGAIN_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Buttons.BACK_TO_MENU_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Commands.ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.FAILED_ADD_FINANCE_TEXT
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.SUCCESSFUL_ADD_FINANCE_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.FinanceClient
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.isTextMessage
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

@Component
class AddFinancesModelHandler(
    private val telegramService: TelegramService,
    private val financeClient: FinanceClient,
    private val textService: TextService,
    private val userSessionService: UserSessionService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return WAITING_FOR_ADD_FINANCE == request.updateSession.state &&
            request.update.isTextMessage()
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        val financeData = dispatchRequest.update.message.text

        return Mono.just(financeData)
            .filter { it.matches(FINANCE_DATA_REGEX) }
            .switchIfEmpty {
                sendMessageDataIsNotCorrect(dispatchRequest)
                    .then(Mono.empty())
            }
            .flatMap {
                createFinance(dispatchRequest, financeData)
            }
            .flatMap {
                sendMessageCreateFinanceIsSuccessful(dispatchRequest)
            }
            .then(
                userSessionService.updateSession(
                    CONVERSATION_STARTED,
                    dispatchRequest.chatId,
                    dispatchRequest.updateSession.localization
                )
            )
            .thenReturn(Unit)
    }

    private fun sendMessageDataIsNotCorrect(dispatchRequest: UpdateRequest): Mono<Message> {
        val localizationText = textService.textMap[dispatchRequest.updateSession.localization]
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = localizationText?.get(FAILED_ADD_FINANCE_TEXT.name).toString(),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardInOneRow(
                    inlineButton(localizationText?.get(BACK_TO_MENU_BUTTON.name).toString(), MENU.command)
                )
            }
        )
    }

    private fun sendMessageCreateFinanceIsSuccessful(dispatchRequest: UpdateRequest): Mono<Message> {
        val localizationText = textService.textMap[dispatchRequest.updateSession.localization]
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = localizationText?.get(SUCCESSFUL_ADD_FINANCE_TEXT.name).toString(),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardInOneRow(
                    inlineButton(localizationText?.get(ADD_FINANCE_AGAIN_BUTTON.name).toString(), ADD_FINANCE.command),
                    inlineButton(localizationText?.get(BACK_TO_MENU_BUTTON.name).toString(), MENU.command)
                )
            }
        )
    }

    private fun createFinance(dispatchRequest: UpdateRequest, financeData: String): Mono<MongoFinance> {
        return financeClient.createFinance(
            MongoFinance(
                userId = dispatchRequest.chatId,
                financeType = checkDataIncomeOrExpense(financeData),
                amount = financeData.substring(1).split(" ")[0].toDouble(),
                description = financeData.split(" ")[1],
                date = Date()
            )
        )
    }

    private fun checkDataIncomeOrExpense(financeData: String): Finance {
        return when {
            financeData.contains("+") -> INCOME
            financeData.contains("-") -> EXPENSE
            else -> throw IllegalArgumentException("Unknown finance type")
        }
    }

    companion object {
        val FINANCE_DATA_REGEX = Regex("[+-]\\d+ [^\\n\\r]+\$")
    }
}
