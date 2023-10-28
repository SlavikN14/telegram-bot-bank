package com.ajaxproject.financeservice.controller.finance

import com.ajaxproject.financeservice.controller.NatsController
import com.ajaxproject.financeservice.service.FinanceService
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.financeservice.service.toFinanceEnum
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdResponse
import com.ajaxproject.financeservice.service.toProtoFinance
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
        return financeService.getAllFinancesByUserId(request.userId, request.financeType.toFinanceEnum())
            .map {
                buildSuccessResponse(
                    it.map { mongoFinance -> mongoFinance.toProtoFinance() }
                )
            }
            .onErrorResume {
                buildFailureResponse(
                    it.message.toString()
                ).toMono()
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

