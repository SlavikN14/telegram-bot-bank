package com.ajaxproject.financeservice.service

import com.ajaxproject.financeservice.repository.FinanceRepositoryImpl
import com.ajaxproject.financeservice.enums.Finance
import com.ajaxproject.financeservice.enums.Finance.EXPENSE
import com.ajaxproject.financeservice.enums.Finance.INCOME
import com.ajaxproject.financeservice.models.MongoFinance
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class FinanceService(
    private val financeRepositoryImpl: FinanceRepositoryImpl,
) {

    fun getAllFinancesByUserId(userId: Long, financeType: Finance): List<MongoFinance> {
        return financeRepositoryImpl.findByUserIdAndFinanceType(userId, financeType)
    }

    fun addFinance(finance: MongoFinance): MongoFinance {
        return financeRepositoryImpl.save(finance)
    }

    fun deleteFinanceByUserId(id: ObjectId) {
        financeRepositoryImpl.deleteById(id)
    }

    fun getCurrencyBalance(userId: Long): Double {
        return getAllFinancesByUserId(userId, EXPENSE).sumOf { it.amount }
            .let { getAllFinancesByUserId(userId, INCOME).sumOf { it.amount } - it }
    }
}
