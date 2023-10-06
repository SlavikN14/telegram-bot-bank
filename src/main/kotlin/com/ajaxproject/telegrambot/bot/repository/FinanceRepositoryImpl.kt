package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.models.MongoFinance
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class FinanceRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : FinanceRepository {

    override fun findByUserId(
        userId: Long,
        financeType: String,
    ): List<MongoFinance> {
        val query: Query = Query().addCriteria(
            Criteria.where("userId").`is`(userId)
                .andOperator(
                    Criteria.where("financeType").`is`(financeType),
                )
        )
        return mongoTemplate.find(query, MongoFinance::class.java, MongoFinance.COLLECTION_NAME)
    }

    override fun deleteById(id: ObjectId, financeType: String) {
        val query: Query = Query().addCriteria(
            Criteria.where("id").`is`(id)
                .andOperator(
                    Criteria.where("finance").`is`(financeType),
                )
        )
        mongoTemplate.findAndRemove(query, MongoFinance::class.java, MongoFinance.COLLECTION_NAME)
    }

    override fun save(finance: MongoFinance, financeType: String): MongoFinance {
        return mongoTemplate.save(finance, MongoFinance.COLLECTION_NAME)
    }
}
