package com.ajaxproject.telegrambot.model

import com.ajaxproject.internalapi.finance.commonmodels.Currency
import com.ajaxproject.telegrambot.model.MongoCurrency.Companion.COLLECTION_NAME
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@TypeAlias("Currency")
@Document(value = COLLECTION_NAME)
data class MongoCurrency(
    @Id
    @JsonProperty("id")
    val id: String,
    @JsonProperty("currencyCodeA")
    var currencyCodeA: Int,
    @JsonProperty("currencyCodeB")
    var currencyCodeB: Int,
    @JsonProperty("date")
    var date: Date,
    @JsonProperty("rateBuy")
    var rateBuy: Double,
    @JsonProperty("rateSell")
    var rateSell: Double,
) {

    companion object {
        const val COLLECTION_NAME = "currency"
    }
}

fun MongoCurrency.toProtoCurrency(): Currency {
    return Currency.newBuilder()
        .setId(id)
        .setCurrencyCodeA(currencyCodeA)
        .setCurrencyCodeB(currencyCodeB)
        .setDate(date.time)
        .setRateBuy(rateBuy)
        .setRateSell(rateSell)
        .build()
}
