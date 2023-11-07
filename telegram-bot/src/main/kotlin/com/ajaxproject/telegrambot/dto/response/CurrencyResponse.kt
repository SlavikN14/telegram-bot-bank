package com.ajaxproject.telegrambot.dto.response

import com.ajaxproject.telegrambot.model.MongoCurrency
import java.util.Date

data class CurrencyResponse(
    val id: String?,
    val currencyCodeA: Int,
    val currencyCodeB: Int,
    var rateBuy: Double,
    var rateSell: Double,
)

fun CurrencyResponse.toEntity() = MongoCurrency(
    id = "$currencyCodeA$currencyCodeB",
    currencyCodeA = currencyCodeA,
    currencyCodeB = currencyCodeB,
    date = Date(),
    rateBuy = rateBuy,
    rateSell = rateSell
)
