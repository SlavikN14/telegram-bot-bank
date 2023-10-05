package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.models.MongoUser

interface UserRepository {

    fun findByPhoneNumber(number: String): MongoUser?

    fun findAll(): List<MongoUser>

    fun save(user: MongoUser): MongoUser

    fun deleteById(userId: Long)
}
