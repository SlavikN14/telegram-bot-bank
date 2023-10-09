package com.ajaxproject.telegrambot.bot.dto

import com.ajaxproject.telegrambot.bot.models.MongoCurrency

data class MonobankCurrencyExchangeResponse(
    val id: String?,
    val currencyCodeA: Int,
    val currencyCodeB: Int,
    var rateBuy: Double,
    var rateSell: Double,
)

fun MonobankCurrencyExchangeResponse.toEntity() = MongoCurrency(
    id = "$currencyCodeA$currencyCodeB",
    currencyCodeA = currencyCodeA,
    currencyCodeB = currencyCodeB,
    rateBuy = rateBuy,
    rateSell = rateSell
)
