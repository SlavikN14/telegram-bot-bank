package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.models.MongoFinance
import org.bson.types.ObjectId

interface FinanceRepository {

    fun findByUserId(userId: Long, financeType: String): List<MongoFinance>?

    fun save(finance: MongoFinance): MongoFinance

    fun deleteById(id: ObjectId, financeType: String)
}
