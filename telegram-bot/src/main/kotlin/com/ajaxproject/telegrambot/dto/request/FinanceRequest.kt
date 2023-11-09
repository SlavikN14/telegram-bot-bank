package com.ajaxproject.telegrambot.dto.request

import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import java.util.*

data class FinanceRequest(
    val userId: Long,
    val financeType: FinanceType,
    val amount: Double,
    val description: String,
    val date: Date,
)

fun FinanceRequest.toProtoFinance(): FinanceMessage = FinanceMessage.newBuilder()
    .setUserId(userId)
    .setFinanceType(financeType)
    .setAmount(amount)
    .setDescription(description)
    .build()
