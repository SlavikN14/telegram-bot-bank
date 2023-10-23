package com.ajaxproject.internalapi;

object NatsSubject {

    private const val REQUEST_PREFIX = "com.ajaxproject.telegrambot"

    object FinanceRequest {
        const val GET_ALL_FINANCES_BY_ID = "$REQUEST_PREFIX.get_all_finances_by_id"
        const val CREATE_FINANCE = "$REQUEST_PREFIX.create_finance"
        const val DELETE_FINANCE = "$REQUEST_PREFIX.delete_finance"
        const val GET_CURRENT_BALANCE = "$REQUEST_PREFIX.get_current_balance"
    }
}
