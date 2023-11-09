package com.ajaxproject.telegrambot.service

import com.ajaxproject.telegrambot.dto.response.CurrencyResponse
import com.ajaxproject.telegrambot.dto.response.toEntity
import com.ajaxproject.telegrambot.enums.Currency
import com.ajaxproject.telegrambot.kafka.CurrencyKafkaProducer
import com.ajaxproject.telegrambot.model.MongoCurrency
import com.ajaxproject.telegrambot.repository.CurrencyRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class CurrencyService(
    private val currencyRedisRepository: CurrencyRepository,
    private val kafkaProducer: CurrencyKafkaProducer,
) {

    fun addAllCurrency(currencies: Array<CurrencyResponse>): Mono<Unit> {
        return Flux.fromArray(currencies)
            .map { it.toEntity() }
            .filter { checkCurrency(it) }
            .flatMap { currencyRedisRepository.save(it) }
            .doOnNext { kafkaProducer.sendCurrencyUpdatedEventToKafka(it) }
            .then(Mono.just(Unit))
    }

    fun getCurrencyByCode(code: Int): Mono<MongoCurrency> {
        return currencyRedisRepository.findByCode(code)
    }

    private fun checkCurrency(currency: MongoCurrency): Boolean {
        return currency.currencyCodeB == Currency.UAH.code
                && (currency.currencyCodeA == Currency.USD.code
                || currency.currencyCodeA == Currency.EUR.code)
    }
}
