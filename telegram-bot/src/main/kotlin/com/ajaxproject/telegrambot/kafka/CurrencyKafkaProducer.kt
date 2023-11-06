package com.ajaxproject.telegrambot.kafka

import com.ajaxproject.internalapi.KafkaTopics
import com.ajaxproject.internalapi.finance.commonmodels.Currency
import com.ajaxproject.internalapi.finance.output.pubsub.CurrencyUpdatedEvent
import com.ajaxproject.telegrambot.model.MongoCurrency
import com.ajaxproject.telegrambot.model.toProtoCurrency
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderRecord
import reactor.kotlin.core.publisher.toMono

@Component
class CurrencyKafkaProducer(
    private val kafkaSenderCurrencyUpdatedEvent: KafkaSender<String, CurrencyUpdatedEvent>,
) {

    fun sendDeviceUpdatedEventToKafka(currency: MongoCurrency): Mono<Unit> =
        Mono.fromSupplier { currency.toProtoCurrency().mapToDeviceUpdatedEvent() }
            .flatMap {
                kafkaSenderCurrencyUpdatedEvent.send(buildKafkaUpdatedMessage(it)).next()
            }
            .thenReturn(Unit)

    private fun buildKafkaUpdatedMessage(event: CurrencyUpdatedEvent) =
        SenderRecord.create(
            ProducerRecord(
                KafkaTopics.Currency.UPDATE,
                event.currency.id,
                event
            ),
            null
        ).toMono()

    private fun Currency.mapToDeviceUpdatedEvent(): CurrencyUpdatedEvent =
        CurrencyUpdatedEvent.newBuilder()
            .setCurrency(this)
            .build()
}
