package com.ajaxproject.telegrambot.service

import com.ajaxproject.telegrambot.dto.response.CurrencyExchangeResponse
import com.ajaxproject.telegrambot.dto.response.toEntity
import com.ajaxproject.telegrambot.enums.Currency
import com.ajaxproject.telegrambot.kafka.CurrencyKafkaProducer
import com.ajaxproject.telegrambot.model.MongoCurrency
import com.ajaxproject.telegrambot.model.toProtoCurrency
import com.ajaxproject.telegrambot.repository.CacheableRepository
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CurrencyExchangeService(
    private val currencyExchangeCacheableRepository: CacheableRepository<MongoCurrency>,
    private val kafkaProducer: CurrencyKafkaProducer,
) {

    fun addAllCurrency(arrayCurrency: Array<CurrencyExchangeResponse>): Mono<Unit> {
        return Flux.fromArray(arrayCurrency)
            .map { it.toEntity() }
            .filter { checkCurrency(it) }
            .flatMap { currencyExchangeCacheableRepository.save(it) }
            .next()
            .doMonoOnNext { kafkaProducer.sendDeviceUpdatedEventToKafka(it) }
            .thenReturn(Unit)
    }

    fun getCurrencyByCode(code: Int): Mono<List<MongoCurrency>> {
        return currencyExchangeCacheableRepository.findAllByKey(code.toString())
            .collectList()
    }

    private fun checkCurrency(currency: MongoCurrency): Boolean {
        val currencyCodes = Currency.entries.map { it.code }
        return currencyCodes.contains(currency.currencyCodeA) && currencyCodes.contains(currency.currencyCodeB)
    }

}

fun <T : Any> Mono<T>.doMonoOnNext(onNext: (T) -> Mono<*>): Mono<T> = flatMap { onNext(it).thenReturn(it) }

