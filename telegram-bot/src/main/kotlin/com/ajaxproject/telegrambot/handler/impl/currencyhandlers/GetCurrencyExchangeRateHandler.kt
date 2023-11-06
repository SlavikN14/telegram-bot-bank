package com.ajaxproject.telegrambot.handler.impl.currencyhandlers

import com.ajaxproject.telegrambot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.enums.Currency.EUR
import com.ajaxproject.telegrambot.enums.Currency.UAH
import com.ajaxproject.telegrambot.enums.Currency.USD
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.handler.impl.MenuCommandHandler
import com.ajaxproject.telegrambot.model.MongoCurrency
import com.ajaxproject.telegrambot.service.CurrencyExchangeService
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.UserSessionService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class GetCurrencyExchangeRateHandler(
    private val telegramService: TelegramMessageService,
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
