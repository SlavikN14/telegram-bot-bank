package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.enums.Currency
import com.ajaxproject.telegrambot.bot.models.currency.MongoCurrency
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class CurrencyExchangeRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : CurrencyExchangeRepository {
    override fun findByCode(code: Int): List<MongoCurrency> {
        val query: Query = Query().addCriteria(
            Criteria.where("currencyCodeA").`is`(code)
                .orOperator(
                    Criteria.where("currencyCodeB").`in`(Currency.entries.map { it.code }),
                )
        )
        return mongoTemplate.find(query, MongoCurrency::class.java, MongoCurrency.COLLECTION_NAME)
    }

    override fun findAll(): List<MongoCurrency> =
        mongoTemplate.findAll(MongoCurrency::class.java, MongoCurrency.COLLECTION_NAME)

    override fun save(currency: MongoCurrency): MongoCurrency {
        return mongoTemplate.save(currency, MongoCurrency.COLLECTION_NAME)
    }

    override fun deleteById(userId: ObjectId) {
        mongoTemplate.findAndRemove(
            Query(Criteria.where("id").`is`(userId)),
            MongoCurrency::class.java,
            MongoCurrency.COLLECTION_NAME
        )
    }
}
