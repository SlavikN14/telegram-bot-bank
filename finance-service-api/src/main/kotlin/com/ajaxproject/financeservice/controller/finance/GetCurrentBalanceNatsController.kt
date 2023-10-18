package com.ajaxproject.financeservice.controller.finance

import com.ajaxproject.financeservice.controller.NatsController
import com.ajaxproject.financeservice.service.FinanceService
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component

@Component
class GetCurrentBalanceNatsController(
    private val financeService: FinanceService,
) : NatsController<GetCurrentBalanceRequest, GetCurrentBalanceResponse> {

    override val subject: String = NatsSubject.FinanceRequest.GET_CURRENT_BALANCE

    override val parser: Parser<GetCurrentBalanceRequest> = GetCurrentBalanceRequest.parser()

    override fun handle(request: GetCurrentBalanceRequest): GetCurrentBalanceResponse = runCatching {
        val getCurrentBalance = financeService.getCurrencyBalance(request.userId)
        buildSuccessResponse(getCurrentBalance)
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    fun buildSuccessResponse(balance: Double): GetCurrentBalanceResponse =
        GetCurrentBalanceResponse.newBuilder().apply {
            successBuilder
                .setBalance(balance)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetCurrentBalanceResponse =
        GetCurrentBalanceResponse.newBuilder().apply {
            failureBuilder
                .setMessage("Finances find failed by $exception: $message")
        }.build()
}

