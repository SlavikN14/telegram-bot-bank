package com.ajaxproject.financeservice.controller.finance

import com.ajaxproject.financeservice.controller.NatsController
import com.ajaxproject.financeservice.service.FinanceService
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdResponse
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

@Component
class DeleteFinanceByIdNatsController(
    private val financeService: FinanceService,
    override val connection: Connection,
) : NatsController<DeleteFinanceByIdRequest, DeleteFinanceByIdResponse> {

    override val subject: String = NatsSubject.FinanceRequest.DELETE_FINANCE

    override val parser: Parser<DeleteFinanceByIdRequest> = DeleteFinanceByIdRequest.parser()

    override fun handle(request: DeleteFinanceByIdRequest): DeleteFinanceByIdResponse = runCatching {
        financeService.deleteFinanceByUserId(ObjectId(request.id))
        buildSuccessResponse()
    }.getOrElse { exception ->
        buildFailureResponse(exception.javaClass.simpleName, exception.toString())
    }

    private fun buildSuccessResponse(): DeleteFinanceByIdResponse =
        DeleteFinanceByIdResponse.newBuilder().apply {
            successBuilder
                .setMessage("Finance deleted successfully")
        }.build()

    private fun buildFailureResponse(exception: String, message: String): DeleteFinanceByIdResponse =
        DeleteFinanceByIdResponse.newBuilder().apply {
            failureBuilder
                .setMessage("User deleteById failed by $exception: $message")
        }.build()
}
