package com.ajaxproject.financeservice.repository

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.models.MongoFinance
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FinanceRepository {

    fun findByUserIdAndFinanceType(userId: Long, financeType: Finance): Flux<MongoFinance>

    fun save(finance: MongoFinance): Mono<MongoFinance>

    fun removeAllById(userId: Long)
}
