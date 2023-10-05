package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.Commands
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.Currency.EUR
import com.ajaxproject.telegrambot.bot.enums.Currency.UAH
import com.ajaxproject.telegrambot.bot.enums.Currency.USD
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.BACK_TO_MENU
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.models.MongoCurrency
import com.ajaxproject.telegrambot.bot.service.CurrencyExchangeService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component

@Component
class GetCurrencyExchangeRateHandler(
    private val telegramService: TelegramService,
    private val currencyExchangeService: CurrencyExchangeService,
    private val textService: TextService,
    private val userSessionService: UserSessionService
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(
            update = request.update,
            command = arrayOf(USD.code.toString(), EUR.code.toString())
        )
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        val callbackQueryCode = dispatchRequest.update.callbackQuery.data.toInt()
        val arrayCurrency = currencyExchangeService.getCurrencyByCode(callbackQueryCode)
        arrayCurrency.forEach {
            telegramService.sendMessage(
                chatId = dispatchRequest.chatId,
                text = formatCurrencyInfo(it, it.currencyCodeA, it.currencyCodeB)
            )
        }
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(BACK_TO_MENU.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton("Return to menu", Commands.MENU.command)
                    )
                )
            }
        )
        val session: UpdateSession = dispatchRequest.updateSession.apply {
            state = CONVERSATION_STARTED
        }
        userSessionService.saveSession(dispatchRequest.chatId, session)
    }

    private fun formatCurrencyInfo(mongoCurrency: MongoCurrency, codeA: Int, codeB: Int): String {
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
        UAH.code -> UAH.name
        EUR.code -> EUR.name
        USD.code -> USD.name
        else -> {
            throw NotFindCodeException("Code not found")
        }
    }
}

class NotFindCodeException(message: String) : Exception(message)
