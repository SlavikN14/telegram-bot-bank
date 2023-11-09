package com.ajaxproject.telegrambot.repository

import com.ajaxproject.telegrambot.model.MongoCurrency
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CurrencyRepository {

    fun findByCode(code: Int): Mono<MongoCurrency>

    fun findAll(): Flux<MongoCurrency>

    fun save(currency: MongoCurrency): Mono<MongoCurrency>
}
