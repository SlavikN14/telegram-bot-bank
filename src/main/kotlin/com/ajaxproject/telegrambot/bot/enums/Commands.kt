package com.ajaxproject.telegrambot.bot.enums

enum class Commands(
    val command: String
) {

    START("/start"),
    MENU("/menu"),
    CURRENCY("/currency"),
    INCOMES("/incomes"),
    ADD_INCOME("/addIncomes"),
    GET_INCOMES("/getIncomes"),
    EXPENSES("/expenses"),
    ADD_EXPENSE("/addExpenses"),
    GET_EXPENSE("/getExpenses"),
    GET_CURRENT_BALANCE("/getCurrentBalance")
}
