package com.ajaxproject.financeservice.finance.application.ports

import com.ajaxproject.financeservice.finance.domain.Finance
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FinanceServiceInPort {

    fun getAllFinancesByUserId(userId: Long, financeType: String): Flux<Finance>

    fun saveFinance(finance: Finance): Mono<Finance>

    fun removeAllFinancesByUserId(userId: Long): Mono<Unit>

    fun getCurrentBalance(userId: Long): Mono<Double>
}
