package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.MonobankCurrencyExchangeResponse
import com.ajaxproject.telegrambot.bot.dto.toEntity
import com.ajaxproject.telegrambot.bot.models.MongoCurrency
import com.ajaxproject.telegrambot.bot.repository.CurrencyExchangeRepositoryImpl
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CurrencyExchangeService(
    private val currencyExchangeRepository: CurrencyExchangeRepositoryImpl,
) {

    fun addAllCurrency(arrayCurrency: Array<MonobankCurrencyExchangeResponse>): Mono<List<MongoCurrency>> {
        return Flux.fromArray(arrayCurrency)
            .map { it.toEntity() }
            .flatMap { currencyExchangeRepository.save(it) }
            .collectList()
    }

    fun getCurrencyByCode(code: Int): Mono<List<MongoCurrency>> {
        return currencyExchangeRepository.findByCode(code)
            .collectList()
    }
}
