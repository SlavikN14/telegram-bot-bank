package com.ajaxproject.financeservice.repository

import com.ajaxproject.financeservice.model.MongoFinance
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FinanceRepository {

    fun findByUserIdAndFinanceType(userId: Long, financeType: FinanceType): Flux<MongoFinance>

    fun save(finance: MongoFinance): Mono<MongoFinance>

    fun removeAllById(userId: Long): Mono<Unit>
}
