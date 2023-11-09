package com.ajaxproject.financeservice.finance.infrastructure.adapter.nats.controller

import com.ajaxproject.financeservice.finance.application.ports.FinanceServiceInPort
import com.ajaxproject.financeservice.finance.application.service.toUnknownError
import com.ajaxproject.financeservice.finance.infrastructure.adapter.nats.NatsController
import com.ajaxproject.financeservice.finance.infrastructure.mapper.toEntityFinance
import com.ajaxproject.financeservice.finance.infrastructure.mapper.toProtoFinance
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateFinanceNatsController(
    private val financeService: FinanceServiceInPort,
) : NatsController<CreateFinanceRequest, CreateFinanceResponse> {

    override val subject: String = NatsSubject.FinanceRequest.CREATE_FINANCE

    override val parser: Parser<CreateFinanceRequest> = CreateFinanceRequest.parser()

    override fun handle(request: CreateFinanceRequest): Mono<CreateFinanceResponse> {
        return financeService.saveFinance(request.finance.toEntityFinance())
            .map { buildSuccessResponse(it.toProtoFinance()) }
            .onErrorResume {
                buildFailureResponse(it.message.toUnknownError()).toMono()
            }
    }

    private fun buildSuccessResponse(finance: FinanceMessage): CreateFinanceResponse =
        CreateFinanceResponse.newBuilder().apply {
            successBuilder.setFinance(finance)
        }.build()

    private fun buildFailureResponse(message: String): CreateFinanceResponse =
        CreateFinanceResponse.newBuilder().apply {
            failureBuilder.setMessage("Create Finance failed: $message")
        }.build()
}
