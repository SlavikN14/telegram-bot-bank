package com.ajaxproject.telegrambot.handler.impl.currencyhandlers

import com.ajaxproject.telegrambot.enums.Buttons.GET_CURRENCY_EUR_BUTTON
import com.ajaxproject.telegrambot.enums.Buttons.GET_CURRENCY_USD_BUTTON
import com.ajaxproject.telegrambot.enums.Commands
import com.ajaxproject.telegrambot.enums.Currency.EUR
import com.ajaxproject.telegrambot.enums.Currency.USD
import com.ajaxproject.telegrambot.enums.TextPropertyName.CURRENCY_TEXT
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.TextService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.util.KeyboardUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CurrencyButtonHandler(
    private val telegramService: TelegramMessageService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, Commands.CURRENCY.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.getText(dispatchRequest.updateSession.localization, CURRENCY_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardInOneRow(
                    inlineButton(
                        textService.getText(dispatchRequest.updateSession.localization, GET_CURRENCY_USD_BUTTON.name),
                        USD.code.toString()
                    ),
                    inlineButton(
                        textService.getText(dispatchRequest.updateSession.localization, GET_CURRENCY_EUR_BUTTON.name),
                        EUR.code.toString()
                    )
                )
            }
        ).thenReturn(Unit)
    }
}
