package com.ajaxproject.financeservice.controller.finance

import com.ajaxproject.financeservice.controller.NatsController
import com.ajaxproject.financeservice.model.toProtoFinance
import com.ajaxproject.financeservice.service.FinanceService
import com.ajaxproject.financeservice.service.toUnknownError
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
    private val financeService: FinanceService,
) : NatsController<GetAllFinancesByIdRequest, GetAllFinancesByIdResponse> {

    override val subject: String = NatsSubject.FinanceRequest.GET_ALL_FINANCES_BY_ID

    override val parser: Parser<GetAllFinancesByIdRequest> = GetAllFinancesByIdRequest.parser()

    override fun handle(request: GetAllFinancesByIdRequest): Mono<GetAllFinancesByIdResponse> {
        return financeService.getAllFinancesByUserId(request.userId, request.financeType)
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
