package com.ajaxproject.telegrambot.bot.dto

import com.ajaxproject.telegrambot.bot.models.MongoFinance
import java.util.*

data class ExpenseResponse(
    val amount: Double,
    val description: String,
    val date: Date,
) {

    override fun toString(): String {
        return """
            |Expense details:
            |Amount: $amount
            |Description: $description
            |Date: $date
        """.trimMargin()
    }
}

fun MongoFinance.toExpenseResponse() = ExpenseResponse(
    amount = amount,
    description = description,
    date = date
)
