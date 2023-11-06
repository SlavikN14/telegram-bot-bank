package com.ajaxproject.telegrambot.model

import com.ajaxproject.telegrambot.model.MongoUser.Companion.COLLECTION_NAME
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
