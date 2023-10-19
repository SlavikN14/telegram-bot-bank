package com.ajaxproject.financeservice.repository

import com.ajaxproject.financemodelsapi.enums.Finance
import com.ajaxproject.financemodelsapi.models.MongoFinance
import org.bson.types.ObjectId

interface FinanceRepository {

    fun findByUserIdAndFinanceType(userId: Long, financeType: Finance): List<MongoFinance>

    fun save(finance: MongoFinance): MongoFinance

    fun deleteById(id: ObjectId)
}
