package com.ajaxproject.telegrambot.repository

import com.ajaxproject.telegrambot.model.MongoCurrency
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CurrencyRepository {

    fun findAllByCode(code: Int): Flux<MongoCurrency>

    fun findAll(): Flux<MongoCurrency>

    fun save(currency: MongoCurrency): Mono<MongoCurrency>
}
