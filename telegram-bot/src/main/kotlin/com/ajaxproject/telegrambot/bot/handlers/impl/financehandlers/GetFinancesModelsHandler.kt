package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.telegrambot.bot.dto.toFinanceResponse
import com.ajaxproject.telegrambot.bot.enums.Buttons
import com.ajaxproject.telegrambot.bot.enums.Commands
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_EXPENSES
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.BACK_TO_MENU_TEXT
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.NO_FINANCE_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.FinanceClient
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.textIsNotUploaded
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Message
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class GetFinancesModelsHandler(
    private val telegramService: TelegramService,
    private val financeClient: FinanceClient,
    private val userSessionService: UserSessionService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, GET_INCOMES.command, GET_EXPENSES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return financeClient.getAllFinancesByUserId(
            dispatchRequest.chatId,
            dispatchRequest.update.callbackQuery.data.checkCommandIncomeOrExpense()
        )
            .flatMapMany { list ->
                Flux.fromIterable(list)
                    .map { it.toFinanceResponse() }
            }
            .switchIfEmpty(
                returnNoFinanceMessage(dispatchRequest)
                    .then(Mono.empty())
            )
            .flatMap { financeResponse ->
                telegramService.sendMessage(
                    chatId = dispatchRequest.chatId,
                    text = financeResponse.toString()
                )
            }
            .flatMap {
                userSessionService.updateSession(
                    CONVERSATION_STARTED,
                    dispatchRequest.chatId,
                    dispatchRequest.updateSession.localization
                )
                    .then(returnToMainMenu(dispatchRequest))
            }
            .then(Mono.empty())
    }

    fun returnNoFinanceMessage(dispatchRequest: UpdateRequest): Mono<Message> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.textMap[dispatchRequest.updateSession.localization]
                ?.get(NO_FINANCE_TEXT.name).textIsNotUploaded(),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardWithManyRows(
                    inlineButton(
                        textService.textMap[dispatchRequest.updateSession.localization]
                            ?.get(Buttons.BACK_TO_MENU_BUTTON.name).textIsNotUploaded(),
                        callbackData = Commands.MENU.command
                    )
                )
            }
        )
    }

    fun returnToMainMenu(dispatchRequest: UpdateRequest): Mono<Message> {
        val localizationText = textService.textMap[dispatchRequest.updateSession.localization]
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = localizationText?.get(BACK_TO_MENU_TEXT.name).textIsNotUploaded(),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardWithManyRows(
                    inlineButton(
                        localizationText?.get(Buttons.BACK_TO_MENU_BUTTON.name).textIsNotUploaded(),
                        Commands.MENU.command
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
