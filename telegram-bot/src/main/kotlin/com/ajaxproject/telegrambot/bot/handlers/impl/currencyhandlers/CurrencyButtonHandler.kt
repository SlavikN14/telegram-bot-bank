package com.ajaxproject.telegrambot.bot.handlers.impl.currencyhandlers

import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_CURRENCY_USD
import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_CURRENCY_EUR
import com.ajaxproject.telegrambot.bot.enums.Commands
import com.ajaxproject.telegrambot.bot.enums.Currency
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component

@Component
class CurrencyButtonHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, Commands.CURRENCY.command)
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(TextPropertyName.CURRENCY_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton(textService.readText(GET_CURRENCY_USD.name), Currency.USD.code.toString()),
                        inlineButton(textService.readText(GET_CURRENCY_EUR.name), Currency.EUR.code.toString())
                    )
                )
            }
        )
    }
}
