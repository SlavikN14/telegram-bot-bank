package com.ajaxproject.financeservice.controller.finance

import com.ajaxproject.financeservice.controller.NatsController
import com.ajaxproject.financeservice.service.FinanceService
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.financeservice.dto.toMongoFinance
import com.ajaxproject.internalapi.finance.commonmodels.FinanceMessage
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceResponse
import com.ajaxproject.financeservice.dto.toProtoFinance
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Component

@Component
class CreateFinanceNatsController(
    private val financeService: FinanceService,
    override val connection: Connection,
) : NatsController<CreateFinanceRequest, CreateFinanceResponse> {

    override val subject: String = NatsSubject.FinanceRequest.CREATE_FINANCE

    override val parser: Parser<CreateFinanceRequest> = CreateFinanceRequest.parser()

    override fun handle(request: CreateFinanceRequest): CreateFinanceResponse = runCatching {
        val savedFinance = financeService.addFinance(request.finance.toMongoFinance())
        buildSuccessResponse(savedFinance.toProtoFinance())
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(finance: FinanceMessage): CreateFinanceResponse =
        CreateFinanceResponse.newBuilder().apply {
            successBuilder
                .setFinance(finance)
        }.build()

    private fun buildFailureResponse(exception: String, message: String): CreateFinanceResponse =
        CreateFinanceResponse.newBuilder().apply {
            failureBuilder
                .setMessage("Create Finance failed by $exception: $message")
        }.build()
}
