package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.models.MongoUser
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
class UserRepositoryImpl(
    private val mongoTemplate: ReactiveMongoTemplate,
) : UserRepository {

    override fun findByPhoneNumber(number: String): Mono<MongoUser?> {
        return mongoTemplate.findOne(
            Query(Criteria.where("phoneNumber").`is`(number)),
            MongoUser::class.java
        )
    }

    override fun findAll(): Flux<MongoUser> {
        return mongoTemplate.findAll(MongoUser::class.java)
    }

    override fun save(user: MongoUser): Mono<MongoUser> {
        return mongoTemplate.save(user)
    }

    override fun deleteById(userId: Long): Mono<MongoUser> {
        return mongoTemplate.findAndRemove(
            Query(Criteria.where("id").`is`(userId)),
            MongoUser::class.java
        )
    }
}
