package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.MonobankCurrencyExchangeResponse
import com.ajaxproject.telegrambot.bot.utils.JsonUtils
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

    @Scheduled(fixedRate = 300000)
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

    private fun parseResponse(response: String): Mono<Array<MonobankCurrencyExchangeResponse>> {
        return Mono.justOrEmpty(JsonUtils.GSON.fromJson(response, Array<MonobankCurrencyExchangeResponse>::class.java))
    }

    companion object {
        private val log = LoggerFactory.getLogger(MonobankClient::class.java)
    }
}
