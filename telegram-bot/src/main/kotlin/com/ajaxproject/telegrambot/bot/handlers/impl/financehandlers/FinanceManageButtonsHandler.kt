package com.ajaxproject.telegrambot.bot.handlers.impl.financehandlers

import com.ajaxproject.telegrambot.bot.enums.Buttons.ADD_FINANCE_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_ALL_EXPENSES_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_ALL_INCOMES_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Commands.ADD_FINANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_EXPENSES
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_INCOMES
import com.ajaxproject.telegrambot.bot.enums.Commands.MANAGE_FINANCES
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.FINANCE_MANAGE_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.textIsNotUploaded
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class FinanceManageButtonsHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, MANAGE_FINANCES.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        val localizationText = textService.textMap[dispatchRequest.updateSession.localization]
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = localizationText?.get(FINANCE_MANAGE_TEXT.name).textIsNotUploaded(),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardWithManyRows(
                    inlineButton(
                        localizationText?.get(ADD_FINANCE_BUTTON.name).textIsNotUploaded(),
                        ADD_FINANCE.command
                    ),
                    inlineButton(
                        localizationText?.get(GET_ALL_INCOMES_BUTTON.name).textIsNotUploaded(),
                        GET_INCOMES.command
                    ),
                    inlineButton(
                        localizationText?.get(GET_ALL_EXPENSES_BUTTON.name).textIsNotUploaded(),
                        GET_EXPENSES.command
                    )
                )
            }
        ).thenReturn(Unit)
    }
}
