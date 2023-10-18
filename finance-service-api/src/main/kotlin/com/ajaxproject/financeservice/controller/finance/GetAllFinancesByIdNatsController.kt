package com.ajaxproject.financeservice.controller.finance

import com.ajaxproject.financeservice.controller.NatsController
import com.ajaxproject.financeservice.service.FinanceService
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.financeservice.dto.toFinanceEnum
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdResponse
import com.ajaxproject.financeservice.dto.toProtoFinance
import com.google.protobuf.Parser
import org.springframework.stereotype.Component

@Component
class GetAllFinancesByIdNatsController(
    private val financeService: FinanceService,
) : NatsController<GetAllFinancesByIdRequest, GetAllFinancesByIdResponse> {

    override val subject: String = NatsSubject.FinanceRequest.GET_ALL_FINANCES_BY_ID

    override val parser: Parser<GetAllFinancesByIdRequest> = GetAllFinancesByIdRequest.parser()

    override fun handle(request: GetAllFinancesByIdRequest): GetAllFinancesByIdResponse = runCatching {
        val getAllFinanceById =
            financeService.getAllFinancesByUserId(request.userId, request.financeType.toFinanceEnum())

        buildSuccessResponse(getAllFinanceById.map { it.toProtoFinance() })
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(financeList: List<FinanceMessage>): GetAllFinancesByIdResponse =
        GetAllFinancesByIdResponse.newBuilder().apply {
            successBuilder.addAllFinance(financeList)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): GetAllFinancesByIdResponse =
        GetAllFinancesByIdResponse.newBuilder().apply {
            failureBuilder
                .setMessage("Finances find failed by $exception: $message")
        }.build()
}

