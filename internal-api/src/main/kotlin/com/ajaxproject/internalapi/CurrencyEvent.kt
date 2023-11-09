package com.ajaxproject.internalapi

object CurrencyEvent {

    private const val SERVICE_NAME = "telegram_bot"

    private const val PREFIX = "output.pubsub"

    const val UPDATED = "updated"

    fun createCurrencyEventKafkaTopic(eventType: String): String =
        "$SERVICE_NAME.$PREFIX.$eventType"

    fun createCurrencyEventNatsSubject(currencyId: Int, eventType: String): String =
        "$SERVICE_NAME.$PREFIX.$currencyId.$eventType"
}
