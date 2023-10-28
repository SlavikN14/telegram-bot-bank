package com.ajaxproject.telegrambot.bot.dto

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.models.MongoFinance
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
        Finance.INCOME -> FinanceType.INCOME
        Finance.EXPENSE -> FinanceType.EXPENSE
    }
}

fun FinanceType.toFinanceEnum(): Finance {
    return when (this) {
        FinanceType.INCOME -> Finance.INCOME
        FinanceType.EXPENSE -> Finance.EXPENSE
        else -> throw IllegalArgumentException("Unknown finance type")
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
