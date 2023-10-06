package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.enums.Finance
import com.ajaxproject.telegrambot.bot.models.MongoFinance
import org.bson.types.ObjectId

interface FinanceRepository {

    fun findByUserId(userId: Long, financeType: Finance): List<MongoFinance>?

    fun save(finance: MongoFinance): MongoFinance

    fun deleteById(id: ObjectId, financeType: Finance)
}
