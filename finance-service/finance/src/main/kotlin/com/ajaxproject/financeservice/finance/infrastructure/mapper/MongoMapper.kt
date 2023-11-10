package com.ajaxproject.financeservice.finance.infrastructure.mapper

import com.ajaxproject.financeservice.finance.domain.Finance
import com.ajaxproject.financeservice.finance.infrastructure.repository.mongo.entity.MongoFinance
import org.bson.types.ObjectId

fun Finance.toMongoFinance(): MongoFinance =
    MongoFinance(
        id = ObjectId(),
        userId = userId,
        financeType = financeType.toProtoFinanceType(),
        amount = amount,
        description = description,
        date = date,
    )

fun MongoFinance.toEntityFinance(): Finance =
    Finance(
        id = id.toString(),
        userId = userId,
        financeType = financeType.toEntityFinanceType(),
        amount = amount,
        description = description,
        date = date,
    )
