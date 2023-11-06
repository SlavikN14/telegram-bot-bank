package com.ajaxproject.telegrambot.repository

import com.ajaxproject.telegrambot.model.MongoUser
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface UserRepository {

    fun findByPhoneNumber(number: String): Mono<MongoUser?>

    fun findAll(): Flux<MongoUser>

    fun save(user: MongoUser): Mono<MongoUser>

    fun deleteById(userId: Long): Mono<MongoUser>
}
