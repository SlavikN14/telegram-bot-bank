package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.MonobankCurrencyExchangeResponse
import com.ajaxproject.telegrambot.bot.properties.MonobankProperties
import com.ajaxproject.telegrambot.bot.utils.HttpUtils
import com.ajaxproject.telegrambot.bot.utils.JsonUtils
import okhttp3.Request
import okhttp3.Response
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class MonobankService(
    urlProperties: MonobankProperties,
    val currencyExchangeService: CurrencyExchangeService,
) {

    private val request: Request = Request.Builder().url(urlProperties.url).get().build()

    fun getCurrencyExchangeRates(): Array<MonobankCurrencyExchangeResponse> {
        val response: Response = HttpUtils.HTTP_CLIENT
            .newCall(request).execute()
        var arrayResponses = emptyArray<MonobankCurrencyExchangeResponse>()
        if (response.isSuccessful) {
            arrayResponses = JsonUtils.GSON.fromJson(
                response.body?.string(), Array<MonobankCurrencyExchangeResponse>::class.java
            )
            response.body?.close()
        } else {
            log.error("HTTP request failed with code {} and response body: {}", response.code, response.body?.string())
        }
        return arrayResponses
    }

    @Scheduled(fixedRate = 300000)
    fun sendCurrencyExchangeRates() {
        currencyExchangeService.addAllCurrency(getCurrencyExchangeRates())
        log.info("Update data in database")
    }

    companion object {
        private val log = LoggerFactory.getLogger(MonobankService::class.java)
    }
}
