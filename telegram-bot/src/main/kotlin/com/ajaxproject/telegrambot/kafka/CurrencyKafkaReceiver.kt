package com.ajaxproject.telegrambot.kafka

import com.ajaxproject.internalapi.finance.output.pubsub.CurrencyUpdatedEvent
import com.ajaxproject.telegrambot.nats.CurrencyEventNatsService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component
import reactor.core.scheduler.Schedulers
import reactor.kafka.receiver.KafkaReceiver

@Component
class CurrencyKafkaReceiver(
    private val kafkaReceiver: KafkaReceiver<String, CurrencyUpdatedEvent>,
    private val currencyUpdateNatsService: CurrencyEventNatsService,
) {
    @PostConstruct
    fun init() {
        kafkaReceiver.receiveAutoAck()
            .flatMap { fluxRecord ->
                fluxRecord.map { currencyUpdateNatsService.publishEvent(it.value()) }
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }
}
