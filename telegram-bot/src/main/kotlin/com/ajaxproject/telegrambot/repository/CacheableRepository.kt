package com.ajaxproject.telegrambot.repository

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CacheableRepository<T> {

    fun findAllByKey(key: String): Flux<T>

    fun save(entity: T): Mono<T>
}
