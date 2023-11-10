package com.ajaxproject.financeservice.finance.application.service

import com.ajaxproject.financeservice.finance.application.ports.FinanceRepositoryOutPort
import com.ajaxproject.financeservice.finance.application.ports.FinanceServiceInPort
import com.ajaxproject.financeservice.finance.domain.Finance
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2

@Service
class FinanceService(
    private val financeRepository: FinanceRepositoryOutPort,
) : FinanceServiceInPort {

    override fun getAllFinancesByUserId(userId: Long, financeType: String): Flux<Finance> {
        return financeRepository.findByUserIdAndFinanceType(userId, financeType)
    }

    override fun saveFinance(finance: Finance): Mono<Finance> {
        return financeRepository.save(finance)
    }

    override fun removeAllFinancesByUserId(userId: Long): Mono<Unit> {
        return financeRepository.removeAllById(userId)
    }

    override fun getCurrentBalance(userId: Long): Mono<Double> {
        return Mono.zip(
            getAllIncomesByUserId(userId),
            getAllExpensesByUserId(userId)
        )
            .map { (incomes, expenses) -> incomes - expenses }
    }

    private fun getAllIncomesByUserId(userId: Long): Mono<Double> {
        return financeRepository.findByUserIdAndFinanceType(userId, "INCOME")
            .reduceWith({ 0.0 }) { acc, finance -> acc + finance.amount }
            .switchIfEmpty { 0.0.toMono() }
    }

    private fun getAllExpensesByUserId(userId: Long): Mono<Double> {
        return financeRepository.findByUserIdAndFinanceType(userId, "EXPENSE")
            .reduceWith({ 0.0 }) { acc, finance -> acc + finance.amount }
            .switchIfEmpty { 0.0.toMono() }
    }
}

fun String?.toUnknownError(): String {
    return this ?: "Unknown error"
}
