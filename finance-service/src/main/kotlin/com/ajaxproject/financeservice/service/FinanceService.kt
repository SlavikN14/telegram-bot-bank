package com.ajaxproject.financeservice.service

import com.ajaxproject.financeservice.repository.FinanceRepository
import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.enums.Finance.EXPENSE
import com.ajaxproject.financemodels.enums.Finance.INCOME
import com.ajaxproject.financemodels.models.MongoFinance
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.util.*

@Service
class FinanceService(
    private val financeRepositoryImpl: FinanceRepository,
) {

    fun getAllFinancesByUserId(userId: Long, financeType: Finance): List<MongoFinance> {
        return financeRepositoryImpl.findByUserIdAndFinanceType(userId, financeType)
    }

    fun addFinance(finance: MongoFinance): MongoFinance {
        return financeRepositoryImpl.save(finance)
    }

    fun deleteFinanceByUserId(id: ObjectId) {
        financeRepositoryImpl.deleteById(id)
    }

    fun getCurrencyBalance(userId: Long): Double {
        return getAllFinancesByUserId(userId, EXPENSE).sumOf { it.amount }
            .let { getAllFinancesByUserId(userId, INCOME).sumOf { it.amount } - it } //TODO: rewrite to Reactor
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
