package com.ajaxproject.financeservice.controller.finance

import com.ajaxproject.financeservice.controller.NatsController
import com.ajaxproject.financeservice.service.FinanceService
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceResponse
import com.ajaxproject.financeservice.service.toProtoFinance
import com.ajaxproject.financeservice.service.toMongoFinance
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class CreateFinanceNatsController(
    private val financeService: FinanceService,
) : NatsController<CreateFinanceRequest, CreateFinanceResponse> {

    override val subject: String = NatsSubject.FinanceRequest.CREATE_FINANCE

    override val parser: Parser<CreateFinanceRequest> = CreateFinanceRequest.parser()

    override fun handle(request: CreateFinanceRequest): Mono<CreateFinanceResponse> {
        return financeService.addFinance(request.finance.toMongoFinance())
            .map { buildSuccessResponse(it.toProtoFinance()) }
            .onErrorResume {
                buildFailureResponse(
                    it.message.toString()
                ).toMono()
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
