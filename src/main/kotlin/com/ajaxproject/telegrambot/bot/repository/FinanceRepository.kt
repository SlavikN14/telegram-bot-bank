package com.ajaxproject.telegrambot.bot.repository

import org.bson.types.ObjectId

interface FinanceRepository<T> {

    fun findByUserId(userId: Long, entityType: Class<T>, collectionName: String): List<T>

    fun save(finance: T, collectionName: String): T

    fun deleteById(id: ObjectId, entityType: Class<T>, collectionName: String)
}
