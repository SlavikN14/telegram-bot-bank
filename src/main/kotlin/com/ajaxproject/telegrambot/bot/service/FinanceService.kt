package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.models.MongoExpense
import com.ajaxproject.telegrambot.bot.models.MongoIncome
import com.ajaxproject.telegrambot.bot.repository.FinanceRepositoryImpl
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class FinanceService(
    private val incomeRepositoryImpl: FinanceRepositoryImpl<MongoIncome>,
    private val expenseRepositoryImpl: FinanceRepositoryImpl<MongoExpense>,
) {

    fun getIncomeByUserId(userId: Long): List<MongoIncome> {
        return incomeRepositoryImpl.findByUserId(userId, MongoIncome::class.java, MongoIncome.COLLECTION_NAME)
    }

    fun getExpenseByUserId(userId: Long): List<MongoExpense> {
        return expenseRepositoryImpl.findByUserId(userId, MongoExpense::class.java, MongoExpense.COLLECTION_NAME)
    }

    fun addIncome(income: MongoIncome): MongoIncome {
        return incomeRepositoryImpl.save(income, MongoIncome.COLLECTION_NAME)
    }

    fun addExpense(expense: MongoExpense): MongoExpense {
        return expenseRepositoryImpl.save(expense, MongoExpense.COLLECTION_NAME)
    }

    fun deleteIncomeById(id: ObjectId) {
        incomeRepositoryImpl.deleteById(id, MongoIncome::class.java, MongoIncome.COLLECTION_NAME)
    }

    fun deleteExpenseById(id: ObjectId) {
        expenseRepositoryImpl.deleteById(id, MongoExpense::class.java, MongoExpense.COLLECTION_NAME)
    }

    fun getCurrencyBalance(userId: Long): String {
        val currentBalance =
            getIncomeByUserId(userId).sumOf { it.amount } - getExpenseByUserId(userId).sumOf { it.amount }
        return "Your current balance is $currentBalance"
    }
}
