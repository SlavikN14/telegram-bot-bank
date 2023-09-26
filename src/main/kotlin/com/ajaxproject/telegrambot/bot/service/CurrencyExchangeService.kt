package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.MonobankCurrencyExchangeResponse
import com.ajaxproject.telegrambot.bot.dto.toEntity
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
        return currencyExchangeRepository.findByCode(code)
    }
}
