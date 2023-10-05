package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.models.MongoUser
import com.ajaxproject.telegrambot.bot.repository.UserRepositoryImpl
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepositoryImpl: UserRepositoryImpl,
) {

    fun getUserByPhoneNumber(phoneNumber: String): MongoUser? {
        return userRepositoryImpl.findByPhoneNumber(phoneNumber)
    }

    fun getAllUsers(): List<MongoUser> {
        return userRepositoryImpl.findAll()
    }

    fun addUser(user: MongoUser): MongoUser {
        return userRepositoryImpl.save(user)
    }

    fun deleteUserById(id: Long) {
        userRepositoryImpl.deleteById(id)
    }
}
