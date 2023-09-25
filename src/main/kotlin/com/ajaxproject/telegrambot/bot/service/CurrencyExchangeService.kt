package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.MonobankCurrencyExchangeResponse
import com.ajaxproject.telegrambot.bot.dto.toEntity
import com.ajaxproject.telegrambot.bot.enums.Currency
import com.ajaxproject.telegrambot.bot.models.currency.MongoCurrency
import com.ajaxproject.telegrambot.bot.repository.CurrencyExchangeRepositoryImpl
import org.springframework.stereotype.Component

@Component
class CurrencyExchangeService(
    private val currencyExchangeRepository: CurrencyExchangeRepositoryImpl,
) {
    fun addAllCurrency(arrayCurrency: Array<MonobankCurrencyExchangeResponse>) {
        arrayCurrency.forEach { currencyExchangeRepository.save(it.toEntity()) }
    }

    fun getCurrencyByCode(code: Int): List<MongoCurrency> {
        return currencyExchangeRepository.findByCode(code).filter { it.currencyCodeA == code }
    }
}

fun Int.findNameByCode(): String {
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
