package com.ajaxproject.telegrambot.bot.repository

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
class FinanceRepositoryImpl<T : Any>(
    private val mongoTemplate: MongoTemplate,
) : FinanceRepository<T> {
    override fun findByUserId(userId: Long, entityType: Class<T>, collectionName: String): List<T> {
        return mongoTemplate.find(
            Query(Criteria.where("userId").`is`(userId)),
            entityType,
            collectionName
        )
    }

    override fun deleteById(id: ObjectId, entityType: Class<T>, collectionName: String) {
        mongoTemplate.findAndRemove(
            Query(Criteria.where("id").`is`(id)),
            entityType,
            collectionName
        )
    }

    override fun save(finance: T, collectionName: String): T {
        return mongoTemplate.save(finance, collectionName)
    }
}
