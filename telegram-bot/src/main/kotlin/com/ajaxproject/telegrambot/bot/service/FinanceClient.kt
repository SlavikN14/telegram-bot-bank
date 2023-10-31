package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.financemodels.enums.Finance
import com.ajaxproject.financemodels.models.MongoFinance
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdResponse
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceResponse
import com.ajaxproject.telegrambot.bot.dto.toMongoFinance
import com.ajaxproject.telegrambot.bot.dto.toProtoEnumFinance
import com.ajaxproject.telegrambot.bot.dto.toProtoFinance
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.Duration

@Service
class FinanceClient(
    private val connection: Connection,
) {

    fun getAllFinancesByUserId(userId: Long, financeType: Finance): Mono<List<MongoFinance>> {
        val request: GetAllFinancesByIdRequest = GetAllFinancesByIdRequest.newBuilder()
            .setUserId(userId)
            .setFinanceType(financeType.toProtoEnumFinance())
            .build()
        val response = doRequest(
            NatsSubject.FinanceRequest.GET_ALL_FINANCES_BY_ID,
            request,
            GetAllFinancesByIdResponse.parser()
        )
        return response
            .flatMap { it.success.financeList.map { financeMessage -> financeMessage.toMongoFinance() }.toMono() }
    }

    fun createFinance(finance: MongoFinance): Mono<MongoFinance> {
        val request: CreateFinanceRequest = CreateFinanceRequest.newBuilder()
            .setFinance(finance.toProtoFinance())
            .build()
        val response = doRequest(
            NatsSubject.FinanceRequest.CREATE_FINANCE,
            request,
            CreateFinanceRequest.parser()
        )
        return response.flatMap { it.finance.toMongoFinance().toMono() }
    }

    fun removeAllFinances(userId: Long): Mono<String> {
        val request: DeleteFinanceByIdRequest = DeleteFinanceByIdRequest.newBuilder()
            .setUserId(userId)
            .build()
        val response = doRequest(
            NatsSubject.FinanceRequest.DELETE_FINANCE,
            request,
            DeleteFinanceByIdRequest.parser()
        )
        return response.toString().toMono()
    }

    fun getCurrentBalance(userId: Long): Mono<Double> {
        val request: GetCurrentBalanceRequest = GetCurrentBalanceRequest.newBuilder()
            .setUserId(userId)
            .build()
        val response = doRequest(
            NatsSubject.FinanceRequest.GET_CURRENT_BALANCE,
            request,
            GetCurrentBalanceResponse.parser()
        )
        return response.flatMap { it.success.balance.toMono() }
    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): Mono<ResponseT> {
        val response = connection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(NATS_TIMEOUT)
        )
        return Mono.fromFuture { response }
            .map { parser.parseFrom(it.data) }
    }

    companion object {
        private const val NATS_TIMEOUT = 10L
    }
}
