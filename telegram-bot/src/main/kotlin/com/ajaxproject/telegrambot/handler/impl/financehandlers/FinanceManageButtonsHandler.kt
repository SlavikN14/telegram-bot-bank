package com.ajaxproject.telegrambot.handler.impl.financehandlers

import com.ajaxproject.telegrambot.enums.Buttons.ADD_FINANCE_BUTTON
import com.ajaxproject.telegrambot.enums.Buttons.GET_ALL_EXPENSES_BUTTON
import com.ajaxproject.telegrambot.enums.Buttons.GET_ALL_INCOMES_BUTTON
import com.ajaxproject.telegrambot.enums.Commands.ADD_FINANCE
import com.ajaxproject.telegrambot.enums.Commands.GET_EXPENSES
import com.ajaxproject.telegrambot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.enums.Commands.MANAGE_FINANCES
import com.ajaxproject.telegrambot.enums.TextPropertyName.FINANCE_MANAGE_TEXT
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.TextService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.util.KeyboardUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FinanceManageButtonsHandler(
    private val telegramService: TelegramMessageService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, MANAGE_FINANCES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.getText(dispatchRequest.updateSession.localization, FINANCE_MANAGE_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardWithManyRows(
                    inlineButton(
                        textService.getText(dispatchRequest.updateSession.localization, ADD_FINANCE_BUTTON.name),
                        ADD_FINANCE.command
                    ),
                    inlineButton(
                        textService.getText(dispatchRequest.updateSession.localization, GET_ALL_INCOMES_BUTTON.name),
                        GET_INCOMES.command
                    ),
                    inlineButton(
                        textService.getText(dispatchRequest.updateSession.localization, GET_ALL_EXPENSES_BUTTON.name),
                        GET_EXPENSES.command
                    )
                )
            }
        ).thenReturn(Unit)
    }
}
