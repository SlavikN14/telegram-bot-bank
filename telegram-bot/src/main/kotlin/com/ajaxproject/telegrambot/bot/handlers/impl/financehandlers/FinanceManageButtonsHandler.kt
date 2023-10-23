package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_ALL_INCOMES
import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_ALL_EXPENSES
import com.ajaxproject.telegrambot.bot.enums.Buttons.ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.Commands
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component

@Component
class FinanceManageButtonsHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, Commands.MANAGE_FINANCES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(TextPropertyName.FINANCE_MANAGE_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(inlineButton(textService.readText(ADD_FINANCE.name), Commands.ADD_FINANCE.command)),
                    inlineRowKeyboard(inlineButton(textService.readText(GET_ALL_INCOMES.name), Commands.GET_INCOMES.command)),
                    inlineRowKeyboard(inlineButton(textService.readText(GET_ALL_EXPENSES.name), Commands.GET_EXPENSES.command))
                )
            }
        )
    }
}
