package com.ajaxproject.financeservice.finance.infrastructure.adapter.nats.controller

import com.ajaxproject.financeservice.finance.application.ports.FinanceServiceInPort
import com.ajaxproject.financeservice.finance.application.service.toUnknownError
import com.ajaxproject.financeservice.finance.infrastructure.adapter.nats.NatsController
import com.ajaxproject.financeservice.finance.infrastructure.mapper.toEntityFinanceType
import com.ajaxproject.financeservice.finance.infrastructure.mapper.toProtoFinance
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdResponse
import com.google.protobuf.Parser
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class GetAllFinancesByIdNatsController(
    private val financeService: FinanceServiceInPort,
) : NatsController<GetAllFinancesByIdRequest, GetAllFinancesByIdResponse> {

    override val subject: String = NatsSubject.FinanceRequest.GET_ALL_FINANCES_BY_ID

    override val parser: Parser<GetAllFinancesByIdRequest> = GetAllFinancesByIdRequest.parser()

    override fun handle(request: GetAllFinancesByIdRequest): Mono<GetAllFinancesByIdResponse> {
        return financeService.getAllFinancesByUserId(request.userId, request.financeType.toEntityFinanceType())
            .map { it.toProtoFinance() }
            .collectList()
            .map { buildSuccessResponse(it) }
            .onErrorResume {
                buildFailureResponse(it.message.toUnknownError()).toMono()
            }
    }

    private fun buildSuccessResponse(financeList: List<FinanceMessage>): GetAllFinancesByIdResponse =
        GetAllFinancesByIdResponse.newBuilder().apply {
            successBuilder.addAllFinance(financeList)
        }.build()

    private fun buildFailureResponse(message: String): GetAllFinancesByIdResponse =
        GetAllFinancesByIdResponse.newBuilder().apply {
            failureBuilder
                .setMessage("Finances find failed: $message")
        }.build()
}
