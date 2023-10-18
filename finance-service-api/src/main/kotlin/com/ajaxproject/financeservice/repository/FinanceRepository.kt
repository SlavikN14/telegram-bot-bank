package com.ajaxproject.financeservice.repository

import com.ajaxproject.financeservice.enums.Finance
import com.ajaxproject.financeservice.models.MongoFinance
import org.bson.types.ObjectId

interface FinanceRepository {

    fun findByUserIdAndFinanceType(userId: Long, financeType: Finance): List<MongoFinance>

    fun save(finance: MongoFinance): MongoFinance

    fun deleteById(id: ObjectId)
}
