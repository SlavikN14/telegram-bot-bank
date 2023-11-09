package com.ajaxproject.financeservice.finance.domain

import java.util.Date

data class Finance(

    val id: String? = null,
    val userId: Long,
    val financeType: String,
    val amount: Double,
    val description: String,
    val date: Date,
)
