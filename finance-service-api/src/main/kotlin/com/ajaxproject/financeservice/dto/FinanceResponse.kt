package com.ajaxproject.financeservice.dto

import com.ajaxproject.financeservice.enums.Finance
import com.ajaxproject.financeservice.models.MongoFinance
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import java.util.*

data class FinanceResponse(
    val amount: Double,
    val description: String,
    val financeType: String,
    val date: Date,
) {

    override fun toString() =
        """
            |$financeType details:
            |Amount: $amount
            |Description: $description
            |Date: $date
        """.trimMargin()
}

fun MongoFinance.toFinanceResponse() = FinanceResponse(
    amount = amount,
    description = description,
    financeType = financeType.toString(),
    date = date
)

fun MongoFinance.toProtoFinance(): FinanceMessage =
    FinanceMessage.newBuilder()
        .setUserId(userId)
        .setFinanceType(financeType.toProtoEnumFinance())
        .setAmount(amount)
        .setDescription(description)
        .build()

fun FinanceMessage.toMongoFinance(): MongoFinance = MongoFinance(
    userId = userId,
    financeType = financeType.toFinanceEnum(),
    amount = amount,
    description = description,
    date = Date(),
)


fun Finance.toProtoEnumFinance(): FinanceType =
    when (this) {
        Finance.INCOME -> FinanceType.INCOME
        Finance.EXPENSE -> FinanceType.EXPENSE
    }

fun FinanceType.toFinanceEnum(): Finance =
    when (this) {
        FinanceType.INCOME -> Finance.INCOME
        FinanceType.EXPENSE -> Finance.EXPENSE
        else -> throw IllegalArgumentException("Unknown finance type")
    }

