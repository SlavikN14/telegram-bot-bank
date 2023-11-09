package com.ajaxproject.telegrambot.repository.impl

import com.ajaxproject.telegrambot.RedisProperties
import com.ajaxproject.telegrambot.model.MongoCurrency
import com.ajaxproject.telegrambot.repository.CurrencyRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.ScanOptions
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmptyDeferred
import java.time.Duration

@Primary
@Repository
class CurrencyRedisRepository(
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, MongoCurrency>,
    @Qualifier("mongoCurrencyRepository")
    private val currencyRepository: CurrencyRepository,
    private val redisProperties: RedisProperties,
) : CurrencyRepository by currencyRepository {

    override fun findAllByCode(code: Int): Flux<MongoCurrency> {
        return reactiveRedisTemplate.scan(
            ScanOptions
                .scanOptions()
                .match("${redisProperties.prefixKey}$code-*")
                .build()
        )
            .flatMap { reactiveRedisTemplate.opsForValue().get(it) }
            .switchIfEmptyDeferred {
                log.info("Cache miss for currency code: {}", code)
                currencyRepository.findAllByCode(code)
                    .flatMap { currency ->
                        saveToCache(currency)
                    }
            }
    }

    override fun save(currency: MongoCurrency): Mono<MongoCurrency> {
        return currencyRepository.save(currency)
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
        private val log = LoggerFactory.getLogger(CurrencyMongoRepository::class.java)
    }
}
