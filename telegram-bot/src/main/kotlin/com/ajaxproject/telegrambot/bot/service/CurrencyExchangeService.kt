package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.MonobankCurrencyExchangeResponse
import com.ajaxproject.telegrambot.bot.dto.toEntity
import com.ajaxproject.telegrambot.bot.models.MongoCurrency
import com.ajaxproject.telegrambot.bot.repository.CurrencyExchangeRepositoryImpl
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class CurrencyExchangeService(
    private val currencyExchangeRepository: CurrencyExchangeRepositoryImpl,
) {

    fun addAllCurrency(arrayCurrency: Array<MonobankCurrencyExchangeResponse>) {
        arrayCurrency.forEach {
            currencyExchangeRepository.save(it.toEntity())
                .subscribe()
        }
    }

    fun getCurrencyByCode(code: Int): Mono<List<MongoCurrency>> {
        return currencyExchangeRepository.findByCode(code)
            .collectList()
    }
}
