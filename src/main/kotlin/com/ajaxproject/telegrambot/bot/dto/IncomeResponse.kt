package com.ajaxproject.telegrambot.bot.dto

import com.ajaxproject.telegrambot.bot.models.MongoIncome
import java.util.*

data class IncomeResponse(
    val amount: Double,
    val description: String,
    val date: Date,
) {

    override fun toString(): String {
        return """
            |Income details:
            |Amount: $amount
            |Description: $description
            |Date: $date
        """.trimMargin()
    }
}

fun MongoIncome.toResponse() = IncomeResponse(
    amount = amount,
    description = description,
    date = date
)
