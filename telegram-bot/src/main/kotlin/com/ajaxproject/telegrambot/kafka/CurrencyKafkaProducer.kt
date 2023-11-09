package com.ajaxproject.telegrambot.kafka

import com.ajaxproject.internalapi.CurrencyEvent
import com.ajaxproject.internalapi.finance.commonmodels.Currency
import com.ajaxproject.internalapi.finance.output.pubsub.CurrencyUpdatedEvent
import com.ajaxproject.telegrambot.model.MongoCurrency
import com.ajaxproject.telegrambot.model.toProtoCurrency
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class CurrencyKafkaProducer(
    private val kafkaSenderCurrencyUpdatedEvent: KafkaSender<String, CurrencyUpdatedEvent>,
) {

    fun sendCurrencyUpdatedEventToKafka(currency: MongoCurrency) {
        kafkaSenderCurrencyUpdatedEvent.send(
            buildKafkaUpdatedMessage(currency.toProtoCurrency().mapToCurrencyUpdatedEvent())
                .toMono()
        ).subscribe()
    }

    private fun buildKafkaUpdatedMessage(event: CurrencyUpdatedEvent) =
        SenderRecord.create(
            ProducerRecord(
                CurrencyEvent.createCurrencyEventKafkaTopic(CurrencyEvent.UPDATED),
                event.currency.id,
                event
            ),
            null
        )

    private fun Currency.mapToCurrencyUpdatedEvent(): CurrencyUpdatedEvent =
        CurrencyUpdatedEvent.newBuilder()
            .setCurrency(this)
            .build()
}
