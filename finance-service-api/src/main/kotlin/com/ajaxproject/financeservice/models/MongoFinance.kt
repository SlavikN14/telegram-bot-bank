package com.ajaxproject.financeservice.models

import com.ajaxproject.financeservice.enums.Finance
import com.ajaxproject.financeservice.models.MongoFinance.Companion.COLLECTION_NAME
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.util.*

@TypeAlias("Finances")
@Document(value = COLLECTION_NAME)
data class MongoFinance(

    @Id
    val id: ObjectId = ObjectId(),
    val userId: Long,
    val financeType: Finance,
    val amount: Double,
    val description: String,
    val date: Date,
) {

    companion object {
        const val COLLECTION_NAME = "finances"
    }
}
