package com.ajaxproject.financeservice.model

import com.ajaxproject.financeservice.model.MongoFinance.Companion.COLLECTION_NAME
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.util.Date

@TypeAlias("Finances")
@Document(value = COLLECTION_NAME)
data class MongoFinance(

    @Id
    val id: ObjectId? = ObjectId(),
    val userId: Long,
    val financeType: FinanceType,
    val amount: Double,
    val description: String,
    val date: Date,
) {

    companion object {
        const val COLLECTION_NAME = "finances"
    }
}

fun MongoFinance.toProtoFinance(): FinanceMessage = FinanceMessage.newBuilder()
    .setUserId(userId)
    .setFinanceType(financeType)
    .setAmount(amount)
    .setDescription(description)
    .build()

fun FinanceMessage.toMongoFinance(): MongoFinance =
    MongoFinance(
        userId = userId,
        financeType = financeType,
        amount = amount,
        description = description,
        date = Date(),
    )
