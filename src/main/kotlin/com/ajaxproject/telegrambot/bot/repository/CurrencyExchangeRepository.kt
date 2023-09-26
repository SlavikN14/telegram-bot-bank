package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.models.currency.MongoCurrency
import org.bson.types.ObjectId

interface CurrencyExchangeRepository {

    fun findByCode(code: Int): List<MongoCurrency>

    fun findAll(): List<MongoCurrency>

    fun save(currency: MongoCurrency): MongoCurrency

    fun deleteById(userId: ObjectId)
}
