package com.ajaxproject.telegrambot.repository.impl

import com.ajaxproject.telegrambot.RedisProperties
import com.ajaxproject.telegrambot.model.MongoCurrency
import com.ajaxproject.telegrambot.repository.CacheableRepository
import com.ajaxproject.telegrambot.repository.CurrencyExchangeRepository
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmptyDeferred
import java.time.Duration

@Repository
class CurrencyExchangeRedisRepositoryImpl(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, MongoCurrency>,
    private val currencyRepository: CurrencyExchangeRepository,
    private val redisProperties: RedisProperties,
) : CacheableRepository<MongoCurrency> {

    override fun findAllByKey(key: String): Flux<MongoCurrency> {
        return reactiveRedisTemplate.scan(
            ScanOptions
                .scanOptions()
                .match("${redisProperties.prefixKey}$key-*")
                .build()
        )
            .flatMap { reactiveRedisTemplate.opsForValue().get(it) }
            .switchIfEmptyDeferred {
                log.info("Cache miss for currency code: {}", key)
                currencyRepository.findAllByCode(key.toInt())
                    .flatMap { currency ->
                        saveToCache(currency)
                    }
            }
    }

    override fun save(entity: MongoCurrency): Mono<MongoCurrency> {
        return currencyRepository.save(entity)
            .flatMap { saveToCache(it) }
    }

    private fun saveToCache(currency: MongoCurrency): Mono<MongoCurrency> {
        return reactiveRedisTemplate.opsForValue()
            .set(
                "${redisProperties.prefixKey}${currency.currencyCodeA}-${currency.currencyCodeB}",
                currency,
                Duration.ofMinutes(redisProperties.ttlMinutes.toLong())
            )
            .thenReturn(currency)
    }

    companion object {
        private val log = LoggerFactory.getLogger(CurrencyExchangeMongoRepositoryImpl::class.java)
    }
}
