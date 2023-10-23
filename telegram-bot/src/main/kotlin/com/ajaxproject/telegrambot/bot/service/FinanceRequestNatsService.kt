package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceResponse
import com.ajaxproject.telegrambot.bot.dto.toMongoFinance
import com.ajaxproject.telegrambot.bot.dto.toProtoEnumFinance
import com.ajaxproject.telegrambot.bot.dto.toProtoFinance
import com.ajaxproject.financemodelsapi.enums.Finance
import com.ajaxproject.financemodelsapi.models.MongoFinance
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdResponse
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.bson.types.ObjectId
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class FinanceRequestNatsService(
    private val connection: Connection,
) {

    fun requestToGetAllFinancesByUserId(userId: Long, financeType: Finance): List<MongoFinance> {
        val request: GetAllFinancesByIdRequest = GetAllFinancesByIdRequest.newBuilder()
            .setUserId(userId)
            .setFinanceType(financeType.toProtoEnumFinance())
            .build()
        val response = doRequest(
            NatsSubject.FinanceRequest.GET_ALL_FINANCES_BY_ID,
            request,
            GetAllFinancesByIdResponse.parser()
        )
        return response.success.financeList.map { it.toMongoFinance() }
    }

    fun requestToCreateFinance(finance: MongoFinance): MongoFinance{
        val request: CreateFinanceRequest = CreateFinanceRequest.newBuilder()
            .setFinance(finance.toProtoFinance())
            .build()
        val response = doRequest(
            NatsSubject.FinanceRequest.CREATE_FINANCE,
            request,
            CreateFinanceRequest.parser()
        )
        return response.finance.toMongoFinance()
    }

    fun requestToDeleteFinance(id: ObjectId): String {
        val request: DeleteFinanceByIdRequest = DeleteFinanceByIdRequest.newBuilder()
            .setId(id.toHexString())
            .build()
        val response = doRequest(
            NatsSubject.FinanceRequest.DELETE_FINANCE,
            request,
            DeleteFinanceByIdRequest.parser()
        )
        return response.toString()
    }

    fun requestToGetCurrentBalance(userId: Long): Double {
        val request: GetCurrentBalanceRequest = GetCurrentBalanceRequest.newBuilder()
            .setUserId(userId)
            .build()
        val response = doRequest(
            NatsSubject.FinanceRequest.GET_CURRENT_BALANCE,
            request,
            GetCurrentBalanceResponse.parser()
        )
        return response.success.balance
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = connection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(NATS_TIMEOUT)
        )
        return parser.parseFrom(response.get().data)
    }
    companion object{
        private const val NATS_TIMEOUT = 10L
    }
}
