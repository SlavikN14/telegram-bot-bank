package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.models.MongoUser
import com.ajaxproject.telegrambot.bot.repository.UserRepositoryImpl
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userRepositoryImpl: UserRepositoryImpl,
) {

    fun getUserByPhoneNumber(phoneNumber: String): Mono<MongoUser?> {
        return userRepositoryImpl.findByPhoneNumber(phoneNumber)
    }

    fun getAllUsers(): Mono<List<MongoUser>> {
        return userRepositoryImpl.findAll()
            .collectList()
    }

    fun addUser(user: MongoUser): Mono<MongoUser> {
        return userRepositoryImpl.save(user)
    }

    fun deleteUserById(id: Long): Mono<MongoUser> {
        return userRepositoryImpl.deleteById(id)
    }
}
