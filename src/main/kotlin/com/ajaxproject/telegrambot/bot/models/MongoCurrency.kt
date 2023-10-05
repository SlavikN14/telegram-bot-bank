package com.ajaxproject.telegrambot.bot.models

import com.ajaxproject.telegrambot.bot.models.MongoCurrency.Companion.COLLECTION_NAME
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("Currency")
@Document(value = COLLECTION_NAME)
data class MongoCurrency(
    @Id
    val id: String,
    var currencyCodeA: Int,
    var currencyCodeB: Int,
    var rateBuy: Double,
    var rateSell: Double,
) {

    companion object {
        const val COLLECTION_NAME = "currency"
    }
}
