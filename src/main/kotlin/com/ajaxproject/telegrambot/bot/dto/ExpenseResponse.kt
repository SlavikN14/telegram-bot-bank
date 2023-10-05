package com.ajaxproject.telegrambot.bot.dto

import com.ajaxproject.telegrambot.bot.models.MongoExpense
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

fun MongoExpense.toResponse() = ExpenseResponse(
    amount = amount,
    description = description,
    date = date
)
