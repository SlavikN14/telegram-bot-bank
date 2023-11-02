package com.ajaxproject.financeservice.service

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.enums.Finance.EXPENSE
import com.ajaxproject.financemodels.enums.Finance.INCOME
import com.ajaxproject.financemodels.models.MongoFinance
import com.ajaxproject.financeservice.repository.FinanceRepository
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import java.util.Date

@Service
class FinanceService(
    private val financeRepositoryImpl: FinanceRepository,
) {

    fun getAllFinancesByUserId(userId: Long, financeType: Finance): Flux<MongoFinance> {
        return financeRepositoryImpl.findByUserIdAndFinanceType(userId, financeType)
    }

    fun addFinance(finance: MongoFinance): Mono<MongoFinance> {
        return financeRepositoryImpl.save(finance)
    }

    fun removeAllFinancesByUserId(userId: Long): Mono<Unit> {
        return financeRepositoryImpl.removeAllById(userId)
    }

    fun getCurrentBalance(userId: Long): Mono<Double> {
        return Mono.zip(
            getAllIncomesByUserId(userId),
            getAllExpensesByUserId(userId)
        )
            .map { (incomes, expenses) -> incomes - expenses }
    }

    private fun getAllIncomesByUserId(userId: Long): Mono<Double> {
        return financeRepositoryImpl.findByUserIdAndFinanceType(userId, INCOME)
            .reduceWith({ 0.0 }) { acc, finance -> acc + finance.amount }
            .switchIfEmpty { 0.0.toMono() }
    }

    private fun getAllExpensesByUserId(userId: Long): Mono<Double> {
        return financeRepositoryImpl.findByUserIdAndFinanceType(userId, EXPENSE)
            .reduceWith({ 0.0 }) { acc, finance -> acc + finance.amount }
            .switchIfEmpty { 0.0.toMono() }
    }
}

fun String?.toUnknownError(): String {
    return this ?: "Unknown error"
}

fun MongoFinance.toProtoFinance(): FinanceMessage {
    return FinanceMessage.newBuilder()
        .setUserId(userId)
        .setFinanceType(financeType.toProtoEnumFinance())
        .setAmount(amount)
        .setDescription(description)
        .build()
}

fun FinanceMessage.toMongoFinance(): MongoFinance {
    return MongoFinance(
        userId = userId,
        financeType = financeType.toFinanceEnum(),
        amount = amount,
        description = description,
        date = Date(),
    )
}

fun Finance.toProtoEnumFinance(): FinanceType {
    return when (this) {
        INCOME -> FinanceType.INCOME
        EXPENSE -> FinanceType.EXPENSE
    }
}

fun FinanceType.toFinanceEnum(): Finance {
    return when (this) {
        FinanceType.INCOME -> INCOME
        FinanceType.EXPENSE -> EXPENSE
        else -> throw IllegalArgumentException("Unknown finance type")
    }
}
