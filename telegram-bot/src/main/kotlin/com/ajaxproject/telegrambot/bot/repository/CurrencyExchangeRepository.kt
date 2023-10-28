package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.models.MongoCurrency
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CurrencyExchangeRepository {

    fun findByCode(code: Int): Flux<MongoCurrency>

    fun findAll(): Flux<MongoCurrency>

    fun save(currency: MongoCurrency): Mono<MongoCurrency>
}
