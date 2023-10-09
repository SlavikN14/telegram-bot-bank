package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.dto.ExpenseResponse
import com.ajaxproject.telegrambot.bot.dto.IncomeResponse
import com.ajaxproject.telegrambot.bot.dto.toExpenseResponse
import com.ajaxproject.telegrambot.bot.dto.toIncomeResponse
import com.ajaxproject.telegrambot.bot.enums.Finance.EXPENSE
import com.ajaxproject.telegrambot.bot.enums.Finance.INCOME
import com.ajaxproject.telegrambot.bot.models.MongoFinance
import com.ajaxproject.telegrambot.bot.repository.FinanceRepositoryImpl
import org.springframework.stereotype.Service

@Service
class FinanceService(
    private val financeRepositoryImpl: FinanceRepositoryImpl,
) {

    fun getAllIncomesByUserId(userId: Long): List<IncomeResponse> {
        return financeRepositoryImpl.findByUserIdAndFinance(userId, INCOME)
            .map { it.toIncomeResponse() }
    }

    fun getAllExpensesByUserId(userId: Long): List<ExpenseResponse> {
        return financeRepositoryImpl.findByUserIdAndFinance(userId, EXPENSE)
            .map { it.toExpenseResponse() }
    }

    fun addFinance(finance: MongoFinance): MongoFinance {
        return financeRepositoryImpl.save(finance)
    }

    fun getCurrencyBalance(userId: Long): Double {
        return getAllExpensesByUserId(userId).sumOf { it.amount }
            .let { getAllIncomesByUserId(userId).sumOf { it.amount }.minus(it) }
    }
}
