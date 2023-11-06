package com.ajaxproject.internalapi

object KafkaTopics {

    private const val SERVICE_NAME = "telegram_bot"

    object Currency {

        private const val SUBDOMAIN = "currency"

        const val UPDATE = "$SERVICE_NAME.output.pubsub.$SUBDOMAIN.update"
    }
}
