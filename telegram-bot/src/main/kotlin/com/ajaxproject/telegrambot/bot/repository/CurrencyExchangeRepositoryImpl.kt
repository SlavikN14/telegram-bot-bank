package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.enums.Currency
import com.ajaxproject.telegrambot.bot.models.MongoCurrency
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class CurrencyExchangeRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate,
) : CurrencyExchangeRepository {

    override fun findByCode(code: Int): Flux<MongoCurrency> {
        val query: Query = Query().addCriteria(
            Criteria.where("currencyCodeA").`is`(code)
                .orOperator(
                    Criteria.where("currencyCodeB").`in`(currencyCodes),
                )
        )
        return mongoTemplate.find(query, MongoCurrency::class.java, MongoCurrency.COLLECTION_NAME)
    }

    override fun findAll(): Flux<MongoCurrency> =
        mongoTemplate.findAll(MongoCurrency::class.java, MongoCurrency.COLLECTION_NAME)

    override fun save(currency: MongoCurrency): Mono<MongoCurrency> {
        return mongoTemplate.save(currency, MongoCurrency.COLLECTION_NAME)
    }

    companion object {
        private val currencyCodes: List<Int> = Currency.entries.map { it.code }
    }
}
