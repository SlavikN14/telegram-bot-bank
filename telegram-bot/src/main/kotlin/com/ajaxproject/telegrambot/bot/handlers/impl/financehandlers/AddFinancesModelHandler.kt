package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.telegrambot.bot.enums.Commands.ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.ConversationState.WAITING_FOR_ADD_FINANCE
import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.FAILED_ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.SUCCESSFUL_ADD_FINANCE
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.financemodels.models.MongoFinance
import com.ajaxproject.financemodels.enums.Finance.INCOME
import com.ajaxproject.financemodels.enums.Finance.EXPENSE
import com.ajaxproject.telegrambot.bot.enums.Buttons.BACK_TO_MENU_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Buttons.ADD_FINANCE_AGAIN
import com.ajaxproject.telegrambot.bot.service.FinanceRequestNatsService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.isTextMessage
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.switchIfEmpty
import java.util.*

@Component
class AddFinancesModelHandler(
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

        Mono.just(financeData)
            .filter { it.matches(Regex("[+-]\\d+ [^\\n\\r]+\$")) }
            .switchIfEmpty {
                Mono.fromSupplier { sendMessageIfDataIsNotCorrect(chatId) }
                    .then(Mono.empty())
            }.subscribeOn(Schedulers.boundedElastic())
            .flatMap {
                sendRequestToCreateFinance(dispatchRequest, financeData)
            }
            .flatMap {
                Mono.fromSupplier {
                    sendMessageIfSuccessfulCreateFinance(chatId)
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
            .subscribe()
    }

    private fun sendMessageIfDataIsNotCorrect(chatId: Long) {
        telegramService.sendMessage(
            chatId = chatId,
            text = textService.readText(FAILED_ADD_FINANCE.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton(textService.readText(BACK_TO_MENU_BUTTON.name), MENU.command)
                    )
                )
            })
    }

    private fun sendMessageIfSuccessfulCreateFinance(chatId: Long) {
        telegramService.sendMessage(
            chatId = chatId,
            text = textService.readText(SUCCESSFUL_ADD_FINANCE.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton(textService.readText(ADD_FINANCE_AGAIN.name), ADD_FINANCE.command),
                        inlineButton(textService.readText(BACK_TO_MENU_BUTTON.name), MENU.command)
                    )
                )
            }
        )
    }

    private fun sendRequestToCreateFinance(dispatchRequest: UpdateRequest, financeData: String): Mono<MongoFinance> {
        return financeRequestNatsService.requestToCreateFinance(
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
}
