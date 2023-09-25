package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.ConversationState
import com.ajaxproject.telegrambot.bot.enums.Currency
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.models.currency.MongoCurrency
import com.ajaxproject.telegrambot.bot.models.user.UserRequest
import com.ajaxproject.telegrambot.bot.models.user.UserSession
import com.ajaxproject.telegrambot.bot.service.CurrencyExchangeService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.findNameByCode
import com.ajaxproject.telegrambot.bot.utils.TextsUtils
import org.springframework.stereotype.Component


@Component
class GetCurrencyExchangeRateHandler(
    val telegramService: TelegramService,
    val text: TextsUtils,
    val userSessionService: UserSessionService,
    val currencyExchangeService: CurrencyExchangeService,
) : UserRequestHandler {

    override fun isApplicable(request: UserRequest): Boolean {
        return isCommand(
            request.update, command = arrayOf(
                Currency.USD.code.toString(),
                Currency.EUR.code.toString()
            )
        )
    }

    override fun handle(dispatchRequest: UserRequest) {
        val callbackQueryCode = dispatchRequest.update.callbackQuery.data.toInt()
        val arrayCurrency = currencyExchangeService.getCurrencyByCode(callbackQueryCode)
        arrayCurrency.forEach {
            telegramService.sendMessage(
                chatId = dispatchRequest.chatId,
                text = formatCurrencyInfo(it, it.currencyCodeA, it.currencyCodeB)
            )
        }

        val session: UserSession = dispatchRequest.userSession
        session.state = ConversationState.CONVERSATION_STARTED
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }

    fun formatCurrencyInfo(mongoCurrency: MongoCurrency, codeA: Int, codeB: Int): String {
        return "\uD83D\uDCB5 Currency: ${codeA.findNameByCode()} to ${codeB.findNameByCode()}\n\n" +
                "Buy Rate: \uD83D\uDCB0 ${mongoCurrency.rateBuy} \n\n" +
                "Sell Rate: \uD83D\uDCB3 ${mongoCurrency.rateSell}\n\n"
    }

    override val isGlobal: Boolean = true
}
