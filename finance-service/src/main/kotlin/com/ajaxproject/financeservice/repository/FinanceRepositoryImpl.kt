package com.ajaxproject.financeservice.repository

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.models.MongoFinance
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class FinanceRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : FinanceRepository {

    override fun findByUserIdAndFinanceType(
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

    override fun deleteById(id: ObjectId) {
        val query: Query = Query().addCriteria(
            Criteria.where("id").`is`(id)
        )
        mongoTemplate.findAndRemove(query, MongoFinance::class.java)
    }

    override fun save(finance: MongoFinance): MongoFinance {
        return mongoTemplate.save(finance)
    }
}
