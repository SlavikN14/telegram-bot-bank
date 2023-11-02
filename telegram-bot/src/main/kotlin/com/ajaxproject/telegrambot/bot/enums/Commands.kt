package com.ajaxproject.telegrambot.bot.enums

enum class Commands(
    val command: String
) {

    START("/start"),
    MENU("/menu"),
    CURRENCY("/currency"),
    MANAGE_FINANCES("/manage_finances"),
    ADD_FINANCE("/add_finance"),
    GET_INCOMES("/get_income"),
    GET_EXPENSES("/get_expense"),
    GET_CURRENT_BALANCE("/get_current_balance"),
    DELETE_DATA("/delete_bot"),
}
