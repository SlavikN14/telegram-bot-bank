package com.ajaxproject.financeservice.finance.application.ports

import com.ajaxproject.financeservice.finance.domain.Finance
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface FinanceRepositoryOutPort {

    fun findByUserIdAndFinanceType(userId: Long, financeType: String): Flux<Finance>

    fun save(finance: Finance): Mono<Finance>

    fun removeAllById(userId: Long): Mono<Unit>
}
