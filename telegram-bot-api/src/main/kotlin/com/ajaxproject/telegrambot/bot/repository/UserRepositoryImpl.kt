package com.ajaxproject.telegrambot.bot.repository

import com.ajaxproject.telegrambot.bot.models.MongoUser
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : UserRepository {

    override fun findByPhoneNumber(number: String): MongoUser? {
        return mongoTemplate.findOne(
            Query(Criteria.where("phoneNumber").`is`(number)),
            MongoUser::class.java
        )
    }

    override fun findAll(): List<MongoUser> {
        return mongoTemplate.findAll(MongoUser::class.java)
    }

    override fun save(user: MongoUser): MongoUser {
        return mongoTemplate.save(user)
    }

    override fun deleteById(userId: Long) {
        mongoTemplate.findAndRemove(
            Query(Criteria.where("id").`is`(userId)),
            MongoUser::class.java
        )
    }
}
