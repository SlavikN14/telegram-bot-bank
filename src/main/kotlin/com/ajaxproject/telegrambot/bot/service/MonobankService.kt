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
        return if (response.isSuccessful) {
            parseBody(response)
        } else {
            log.error("HTTP request failed with code {} and response body: {}", response.code, response.body?.string())
            emptyArray()
        }
    }

    private fun parseBody(response: Response): Array<MonobankCurrencyExchangeResponse> {
        val body = response.body ?: return emptyArray()
        return body.use {
            JsonUtils.GSON.fromJson(it.string(), Array<MonobankCurrencyExchangeResponse>::class.java)
        }
    }

    @Scheduled(fixedRate = 300000)
    fun sendCurrencyExchangeRates() {
        currencyExchangeService.addAllCurrency(getCurrencyExchangeRates())
        log.info("Updated data in database")
    }

    companion object {
        private val log = LoggerFactory.getLogger(MonobankService::class.java)
    }
}
