package com.ajaxproject.telegrambot.service

import com.ajaxproject.telegrambot.model.MongoUser
import com.ajaxproject.telegrambot.repository.impl.UserMongoRepositoryImpl
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UserService(
    private val userMongoRepositoryImpl: UserMongoRepositoryImpl,
) {

    fun getUserByPhoneNumber(phoneNumber: String): Mono<MongoUser?> {
        return userMongoRepositoryImpl.findByPhoneNumber(phoneNumber)
    }

    fun getAllUsers(): Mono<List<MongoUser>> {
        return userMongoRepositoryImpl.findAll()
            .collectList()
    }

    fun addUser(user: MongoUser): Mono<MongoUser> {
        return userMongoRepositoryImpl.save(user)
    }

    fun deleteUserById(id: Long): Mono<MongoUser> {
        return userMongoRepositoryImpl.deleteById(id)
    }
}
