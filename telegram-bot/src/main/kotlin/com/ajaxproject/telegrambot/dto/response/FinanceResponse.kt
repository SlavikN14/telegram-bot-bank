package com.ajaxproject.telegrambot.dto.response

import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import java.util.Date

data class FinanceResponse(
    val userId: Long,
    val financeType: FinanceType,
    val amount: Double,
    val description: String,
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

fun FinanceMessage.toFinanceResponse(): FinanceResponse {
    return FinanceResponse(
        userId = userId,
        financeType = financeType,
        amount = amount,
        description = description,
        date = Date(),
    )
}
