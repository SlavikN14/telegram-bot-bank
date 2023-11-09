package com.ajaxproject.financeservice.finance.infrastructure.mapper

import com.ajaxproject.financeservice.finance.domain.Finance
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import org.bson.types.ObjectId
import java.util.*

fun Finance.toProtoFinance(): FinanceMessage = FinanceMessage.newBuilder()
    .setUserId(userId)
    .setFinanceType(financeType.toProtoFinanceType())
    .setAmount(amount)
    .setDescription(description)
    .build()

fun FinanceMessage.toEntityFinance(): Finance =
    Finance(
        id = ObjectId().toHexString(),
        userId = userId,
        financeType = financeType.toEntityFinanceType(),
        amount = amount,
        description = description,
        date = Date(),
    )

fun String.toProtoFinanceType(): FinanceType =
    when (this) {
        "INCOME" -> FinanceType.INCOME
        "EXPENSE" -> FinanceType.EXPENSE
        else -> {
            throw IllegalArgumentException("Invalid finance type")
        }
    }

fun FinanceType.toEntityFinanceType(): String =
    when (this) {
        FinanceType.INCOME -> "INCOME"
        FinanceType.EXPENSE -> "EXPENSE"
        else -> {
            throw IllegalArgumentException("Invalid finance type")
        }
    }
