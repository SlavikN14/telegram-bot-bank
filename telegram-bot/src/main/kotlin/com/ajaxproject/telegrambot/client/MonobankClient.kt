package com.ajaxproject.telegrambot.client

import com.ajaxproject.telegrambot.dto.response.CurrencyExchangeResponse
import com.ajaxproject.telegrambot.service.CurrencyExchangeService
import com.ajaxproject.telegrambot.util.JsonUtils
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class MonobankClient(
    private val currencyExchangeService: CurrencyExchangeService,
    private val webClient: WebClient,
) {

    @Scheduled(initialDelay = 30_000, fixedRate = 300_000)
    fun getCurrencyExchangeRates() {
        webClient.get()
            .retrieve()
            .bodyToMono(String::class.java)
            .flatMap { response ->
                parseResponse(response)
            }
            .flatMap { currencyExchangeService.addAllCurrency(it) }
            .doOnSuccess { log.info("Updated data in the database") }
            .doOnError { error ->
                log.error("HTTP request failed with error: ${error.message}")
            }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun parseResponse(response: String): Mono<Array<CurrencyExchangeResponse>> {
        return Mono.justOrEmpty(JsonUtils.GSON.fromJson(response, Array<CurrencyExchangeResponse>::class.java))
    }

    companion object {
        private val log = LoggerFactory.getLogger(MonobankClient::class.java)
    }
}
