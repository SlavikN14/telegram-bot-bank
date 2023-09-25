package com.ajaxproject.telegrambot.bot.models.currency

import com.ajaxproject.telegrambot.bot.models.currency.MongoCurrency.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field

@TypeAlias("Currency")
@Document(value = COLLECTION_NAME)
data class MongoCurrency(
    @Id
    val id: String,
    @Field("currency_code_A")
    var currencyCodeA: Int,
    @Field("currency_code_B")
    var currencyCodeB: Int,
    @Field("rate_buy")
    var rateBuy: Double,
    @Field("rate_sell")
    var rateSell: Double,
) {

    companion object {
        const val COLLECTION_NAME = "currency"
    }
}
