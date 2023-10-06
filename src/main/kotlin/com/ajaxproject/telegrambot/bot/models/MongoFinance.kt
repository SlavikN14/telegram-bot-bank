package com.ajaxproject.telegrambot.bot.models

import com.ajaxproject.telegrambot.bot.models.MongoFinance.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@TypeAlias("Finances")
@Document(value = COLLECTION_NAME)
data class MongoFinance(

    @Id
    val id: ObjectId,
    val userId: Long,
    val financeType: String,
    val amount: Double,
    val description: String,
    val date: Date = Date(),
) {

    companion object {
        const val COLLECTION_NAME = "finances"
    }
}
