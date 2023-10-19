package com.ajaxproject.telegrambot.bot.models

import com.ajaxproject.telegrambot.bot.models.MongoUser.Companion.COLLECTION_NAME
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document

@TypeAlias("User")
@Document(value = COLLECTION_NAME)
data class MongoUser(
    @Id
    val id: Long,
    val phoneNumber: String,
) {

    companion object {
        const val COLLECTION_NAME = "user"
    }
}
