package com.ajaxproject.financeservice.finance.infrastructure.adapter.nats.controller

import com.ajaxproject.financeservice.finance.application.ports.FinanceServiceInPort
import com.ajaxproject.financeservice.finance.application.service.toUnknownError
import com.ajaxproject.financeservice.finance.infrastructure.adapter.nats.NatsController
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetCurrentBalanceNatsController(
    private val financeService: FinanceServiceInPort,
) : NatsController<GetCurrentBalanceRequest, GetCurrentBalanceResponse> {

    override val subject: String = NatsSubject.FinanceRequest.GET_CURRENT_BALANCE

    override val parser: Parser<GetCurrentBalanceRequest> = GetCurrentBalanceRequest.parser()

    override fun handle(request: GetCurrentBalanceRequest): Mono<GetCurrentBalanceResponse> {
        return financeService.getCurrentBalance(request.userId)
            .map { buildSuccessResponse(it) }
            .onErrorResume {
                buildFailureResponse(it.message.toUnknownError()).toMono()
            }
    }

    fun buildSuccessResponse(balance: Double): GetCurrentBalanceResponse =
        GetCurrentBalanceResponse.newBuilder().apply {
            successBuilder.setBalance(balance)
        }.build()

    private fun buildFailureResponse(message: String): GetCurrentBalanceResponse =
        GetCurrentBalanceResponse.newBuilder().apply {
            failureBuilder.setMessage("Finances find failed: $message")
        }.build()
}
