package com.ajaxproject.telegrambot.repository.impl

import com.ajaxproject.telegrambot.model.MongoCurrency
import com.ajaxproject.telegrambot.repository.CurrencyRepository
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository("mongoCurrencyRepository")
class CurrencyMongoRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) : CurrencyRepository {

    override fun findAllByCode(code: Int): Flux<MongoCurrency> {
        val query: Query = Query().addCriteria(
            Criteria.where("currencyCodeA").`is`(code)
        )
        return mongoTemplate.find(query, MongoCurrency::class.java, MongoCurrency.COLLECTION_NAME)
    }

    override fun findAll(): Flux<MongoCurrency> =
        mongoTemplate.findAll(MongoCurrency::class.java, MongoCurrency.COLLECTION_NAME)

    override fun save(currency: MongoCurrency): Mono<MongoCurrency> {
        return mongoTemplate.save(currency, MongoCurrency.COLLECTION_NAME)
    }
}
