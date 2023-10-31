package com.ajaxproject.financeservice.repository

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.models.MongoFinance
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class FinanceRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate,
) : FinanceRepository {

    override fun findByUserIdAndFinanceType(
        userId: Long,
        financeType: Finance,
    ): Flux<MongoFinance> {
        val query: Query = Query().addCriteria(
            Criteria.where("userId").`is`(userId)
                .andOperator(
                    Criteria.where("financeType").`is`(financeType.name),
                )
        )
        return mongoTemplate.find(query, MongoFinance::class.java)
    }

    override fun removeAllById(userId: Long): Mono<Unit> {
        val query: Query = Query().addCriteria(
            Criteria.where("userId").`is`(userId)
        )
        return mongoTemplate.remove(query, MongoFinance::class.java)
            .thenReturn(Unit)
    }

    override fun save(finance: MongoFinance): Mono<MongoFinance> {
        return mongoTemplate.save(finance)
    }
}
