package com.ajaxproject.financeservice.repository

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.models.MongoFinance
import org.bson.types.ObjectId

interface FinanceRepository {

    fun findByUserIdAndFinanceType(userId: Long, financeType: Finance): List<MongoFinance>

    fun save(finance: MongoFinance): MongoFinance

    fun deleteById(id: ObjectId)
}
