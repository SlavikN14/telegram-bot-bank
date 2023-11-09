package com.ajaxproject.telegrambot.nats

import com.ajaxproject.internalapi.CurrencyEvent
import com.ajaxproject.internalapi.finance.output.pubsub.CurrencyUpdatedEvent
import io.nats.client.Connection
import com.google.protobuf.Parser
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class CurrencyEventNatsService(
    private val connection: Connection,
) {

    private val parser: Parser<CurrencyUpdatedEvent> = CurrencyUpdatedEvent.parser()

    private val dispatcher = connection.createDispatcher()

    fun subscribeToEvents(currencyCode: Int, eventType: String): Flux<CurrencyUpdatedEvent> =
        Flux.create { sink ->
            dispatcher.apply {
                subscribe(CurrencyEvent.createCurrencyEventNatsSubject(currencyCode, eventType)) { message ->
                    val parsedData = parser.parseFrom(message.data)
                    sink.next(parsedData)
                }
            }
        }

    fun publishEvent(currencyUpdatedEvent: CurrencyUpdatedEvent) {
        val updateEventSubject =
            CurrencyEvent.createCurrencyEventNatsSubject(
                currencyUpdatedEvent.currency.currencyCodeA,
                CurrencyEvent.UPDATED
            )
        connection.publish(updateEventSubject, currencyUpdatedEvent.toByteArray())
    }
}
