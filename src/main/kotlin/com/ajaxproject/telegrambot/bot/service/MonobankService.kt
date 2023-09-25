package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.MonobankCurrencyExchangeResponse
import com.ajaxproject.telegrambot.bot.properties.MonobankProperties
import com.ajaxproject.telegrambot.bot.utils.HttpUtils
import com.ajaxproject.telegrambot.bot.utils.JsonUtils
import com.squareup.okhttp.Request
import com.squareup.okhttp.Response
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service


@Service
class MonobankService(
    urlProperties: MonobankProperties,
    val currencyExchangeService: CurrencyExchangeService,
) {

    private final val request: Request = Request.Builder().url(urlProperties.url).get().build()

    val response: Response = HttpUtils.HTTP_CLIENT.newCall(request).execute()

    fun getCurrencyExchangeRates(): Array<MonobankCurrencyExchangeResponse> {
        if (response.isSuccessful) {
            return JsonUtils.GSON.fromJson(
                response.body().string(), Array<MonobankCurrencyExchangeResponse>::class.java
            )
        } else {
            throw ResponseIsFailedException("Failed response")
        }
    }

    @Scheduled(fixedRate = 300000)
    fun sendCurrencyExchangeRates() {
        currencyExchangeService.addAllCurrency(getCurrencyExchangeRates())
    }
}

class ResponseIsFailedException(message: String) : Exception(message)
