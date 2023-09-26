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
        return """
            Currency: ${codeA.findNameByCode()} to ${codeB.findNameByCode()}
            
            Buy Rate: ${mongoCurrency.rateBuy}
            
            Sell Rate: ${mongoCurrency.rateSell}
            """.trimIndent()
    }

    override val isGlobal: Boolean = true
}

private fun Int.findNameByCode(): String {
    return when (this) {
        Currency.UAH.code -> Currency.UAH.name
        Currency.EUR.code -> Currency.EUR.name
        Currency.USD.code -> Currency.USD.name
        else -> {
            throw NotFindCodeException("Code not found")
        }
    }
}

class NotFindCodeException(message: String) : Exception(message)
