package com.ajaxproject.telegrambot.bot.handlers.impl.currencyhandlers

import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.Currency.EUR
import com.ajaxproject.telegrambot.bot.enums.Currency.UAH
import com.ajaxproject.telegrambot.bot.enums.Currency.USD
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.handlers.impl.MenuCommandHandler
import com.ajaxproject.telegrambot.bot.models.MongoCurrency
import com.ajaxproject.telegrambot.bot.service.CurrencyExchangeService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class GetCurrencyExchangeRateHandler(
    private val telegramService: TelegramService,
    private val currencyExchangeService: CurrencyExchangeService,
    private val userSessionService: UserSessionService,
    private val menuCommandHandler: MenuCommandHandler
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(
            update = request.update,
            command = arrayOf(USD.code.toString(), EUR.code.toString())
        )
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        val callbackQueryCode = dispatchRequest.update.callbackQuery.data.toInt()

        return currencyExchangeService.getCurrencyByCode(callbackQueryCode)
            .flatMapMany { arrayCurrency ->
                Flux.fromIterable(arrayCurrency)
                    .map { formatCurrencyInfo(it, it.currencyCodeA, it.currencyCodeB) }
            }
            .flatMap { text ->
                telegramService.sendMessage(
                    chatId = dispatchRequest.chatId,
                    text = text
                )
            }
            .flatMap {
                userSessionService.updateSession(
                    CONVERSATION_STARTED,
                    dispatchRequest.chatId,
                    dispatchRequest.updateSession.localization
                )
            }
            .then(
                menuCommandHandler.handle(dispatchRequest)
            )
            .thenReturn(Unit)
    }

    private fun formatCurrencyInfo(mongoCurrency: MongoCurrency, codeA: Int, codeB: Int): String {
        return """
            Currency: ${codeA.findNameByCode()} to ${codeB.findNameByCode()}
            
            Buy Rate: ${mongoCurrency.rateBuy}
            
            Sell Rate: ${mongoCurrency.rateSell}
        """.trimIndent()
    }
}

private fun Int.findNameByCode(): String {
    return when (this) {
        UAH.code -> UAH.name
        EUR.code -> EUR.name
        USD.code -> USD.name
        else -> throw NotFindCodeException("Code not found")
    }
}

class NotFindCodeException(message: String) : Exception(message)
