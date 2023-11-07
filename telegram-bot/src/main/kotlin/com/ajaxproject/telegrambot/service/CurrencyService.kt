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
    private val currencyExchangeCacheableRepository: CurrencyRepository,
    private val kafkaProducer: CurrencyKafkaProducer,
) {

    fun addAllCurrency(currencies: Array<CurrencyResponse>): Mono<Unit> {
        return Flux.fromArray(currencies)
            .map { it.toEntity() }
            .filter { checkCurrency(it) }
            .flatMap { currencyExchangeCacheableRepository.save(it) }
            .next()
            .doMonoOnNext { kafkaProducer.sendCurrencyUpdatedEventToKafka(it) }
            .thenReturn(Unit)
    }

    fun getCurrencyByCode(code: Int): Flux<MongoCurrency> {
        return currencyExchangeCacheableRepository.findAllByCode(code)
    }

    private fun checkCurrency(currency: MongoCurrency): Boolean {
        val currencyCodes = Currency.entries.map { it.code }
        return currencyCodes.contains(currency.currencyCodeA) && currencyCodes.contains(currency.currencyCodeB)
    }

}

fun <T : Any> Mono<T>.doMonoOnNext(onNext: (T) -> Mono<*>): Mono<T> = flatMap { onNext(it).thenReturn(it) }

