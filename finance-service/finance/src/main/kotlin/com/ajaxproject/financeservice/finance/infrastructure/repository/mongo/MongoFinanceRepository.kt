package com.ajaxproject.financeservice.finance.infrastructure.repository.mongo

import com.ajaxproject.financeservice.finance.application.ports.FinanceRepositoryOutPort
import com.ajaxproject.financeservice.finance.domain.Finance
import com.ajaxproject.financeservice.finance.infrastructure.mapper.toEntityFinance
import com.ajaxproject.financeservice.finance.infrastructure.mapper.toMongoFinance
import com.ajaxproject.financeservice.finance.infrastructure.mapper.toProtoFinanceType
import com.ajaxproject.financeservice.finance.infrastructure.repository.mongo.entity.MongoFinance
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class MongoFinanceRepository(
    private val mongoTemplate: ReactiveMongoTemplate,
) : FinanceRepositoryOutPort {

    override fun findByUserIdAndFinanceType(userId: Long, financeType: String): Flux<Finance> {
        val query: Query = Query().addCriteria(
            Criteria.where("userId").`is`(userId)
                .andOperator(
                    Criteria.where("financeType").`is`(financeType.toProtoFinanceType()),
                )
        )
        return mongoTemplate.find(query, MongoFinance::class.java)
            .map { it.toEntityFinance() }
    }

    override fun removeAllById(userId: Long): Mono<Unit> {
        val query: Query = Query().addCriteria(
            Criteria.where("userId").`is`(userId)
        )
        return mongoTemplate.remove(query, MongoFinance::class.java)
            .thenReturn(Unit)
    }

    override fun save(finance: Finance): Mono<Finance> {
        return mongoTemplate.save(finance.toMongoFinance())
            .map { it.toEntityFinance() }
    }
}
