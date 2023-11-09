package com.ajaxproject.telegrambot.grpc

import com.ajaxproject.api.internalapi.finance.input.reqreply.StreamCurrencyByCodeRequest
import com.ajaxproject.api.internalapi.finance.input.reqreply.StreamCurrencyByCodeResponse
import com.ajaxproject.internalapi.CurrencyEvent
import com.ajaxproject.internalapi.finance.commonmodels.Currency
import com.ajaxproject.internalapi.finance.svc.currency_service.proto.ReactorCurrencyServiceGrpc
import com.ajaxproject.telegrambot.model.toProtoCurrency
import com.ajaxproject.telegrambot.nats.CurrencyEventNatsService
import com.ajaxproject.telegrambot.service.CurrencyService
import net.devh.boot.grpc.server.service.GrpcService
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@GrpcService
class GrpcCurrencyService(
    private val currencyService: CurrencyService,
    private val currencyEventNatsService: CurrencyEventNatsService,
) : ReactorCurrencyServiceGrpc.CurrencyServiceImplBase() {

    override fun streamCurrencyByCode(request: Mono<StreamCurrencyByCodeRequest>): Flux<StreamCurrencyByCodeResponse> =
        request.flatMapMany { handleStreamByCodeRequest(it) }

    private fun handleStreamByCodeRequest(request: StreamCurrencyByCodeRequest): Flux<StreamCurrencyByCodeResponse> =
        currencyService.getCurrencyByCode(request.code.toInt())
            .flatMapMany { currency ->
                currencyEventNatsService.subscribeToEvents(currency.currencyCodeA, CurrencyEvent.UPDATED)
                    .map { buildSuccessResponseStreamByCode(it.currency) }
                    .startWith(buildSuccessResponseStreamByCode(currency.toProtoCurrency()))
            }
            .onErrorResume { buildFailureResponseStreamByCode(it.message.toUnknownError()).toMono() }

    private fun buildSuccessResponseStreamByCode(currency: Currency): StreamCurrencyByCodeResponse =
        StreamCurrencyByCodeResponse.newBuilder().apply {
            successBuilder.setCurrency(currency)
        }.build()

    private fun buildFailureResponseStreamByCode(message: String): StreamCurrencyByCodeResponse =
        StreamCurrencyByCodeResponse.newBuilder().apply {
            failureBuilder.setMessage("Currency find by code failed: $message")
        }.build()
}

fun String?.toUnknownError(): String {
    return this ?: "Unknown error"
}
