package com.ajaxproject.telegrambot.bot.enums

enum class Currency(
    val symbol: String,
    val code: Int,
) {

    UAH("₴", code = 980),
    USD("$", code = 840),
    EUR("€", code = 978)
}
