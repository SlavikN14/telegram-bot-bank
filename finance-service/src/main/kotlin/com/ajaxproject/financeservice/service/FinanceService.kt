package com.ajaxproject.financeservice.service

import com.ajaxproject.financeservice.repository.FinanceRepository
import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.enums.Finance.EXPENSE
import com.ajaxproject.financemodels.enums.Finance.INCOME
import com.ajaxproject.financemodels.models.MongoFinance
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.util.*

@Service
class FinanceService(
    private val financeRepositoryImpl: FinanceRepository,
) {

    fun getAllFinancesByUserId(userId: Long, financeType: Finance): Mono<List<MongoFinance>> {
        return financeRepositoryImpl.findByUserIdAndFinanceType(userId, financeType)
            .collectList()
    }

    fun addFinance(finance: MongoFinance): Mono<MongoFinance> {
        return financeRepositoryImpl.save(finance)
    }

    fun removeAllFinancesByUserId(userId: Long): Mono<Unit> {
        return Mono.just(financeRepositoryImpl.removeAllById(userId))
    }

    fun getCurrencyBalance(userId: Long): Mono<Double> {
        return Mono.zip(
            getAllFinancesByUserId(userId, EXPENSE)
                .map { list -> list.sumOf { it.amount } },
            getAllFinancesByUserId(userId, INCOME)
                .map { list -> list.sumOf { it.amount } })
            .flatMap { finances -> (finances.t2.minus(finances.t1)).toMono() }
    }
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
