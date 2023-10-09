package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.enums.Finance
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

    override fun findByUserIdAndFinance(
        userId: Long,
        financeType: Finance,
    ): List<MongoFinance> {
        val query: Query = Query().addCriteria(
            Criteria.where("userId").`is`(userId)
                .andOperator(
                    Criteria.where("financeType").`is`(financeType.name),
                )
        )
        return mongoTemplate.find(query, MongoFinance::class.java)
    }

    override fun deleteById(id: ObjectId, financeType: Finance) {
        val query: Query = Query().addCriteria(
            Criteria.where("id").`is`(id)
                .andOperator(
                    Criteria.where("finance").`is`(financeType.name),
                )
        )
        mongoTemplate.findAndRemove(query, MongoFinance::class.java)
    }

    override fun save(finance: MongoFinance): MongoFinance {
        return mongoTemplate.save(finance)
    }
}
