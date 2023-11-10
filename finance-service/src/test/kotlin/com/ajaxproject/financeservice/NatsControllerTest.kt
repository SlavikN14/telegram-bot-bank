package com.ajaxproject.financeservice

import com.ajaxproject.financeservice.finance.infrastructure.mapper.toEntityFinance
import com.ajaxproject.financeservice.finance.infrastructure.mapper.toProtoFinance
import com.ajaxproject.financeservice.finance.infrastructure.repository.mongo.MongoFinanceRepository
import com.ajaxproject.internalapi.NatsSubject
import com.ajaxproject.financeservice.finance.infrastructure.repository.mongo.entity.MongoFinance
import com.ajaxproject.internalapi.finance.commonmodels.FinanceType
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.CreateFinanceResponse
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.DeleteFinanceByIdResponse
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetAllFinancesByIdResponse
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceRequest
import com.ajaxproject.internalapi.finance.input.reqreply.GetCurrentBalanceResponse
import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser
import io.nats.client.Connection
import org.assertj.core.api.Assertions.assertThat
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import java.time.Duration
import java.util.*


@SpringBootTest
class NatsControllerTest {

    @Autowired
    private lateinit var natsConnection: Connection

    @Autowired
    private lateinit var mongoTemplate: ReactiveMongoTemplate

    @Autowired
    private lateinit var financeRepository: MongoFinanceRepository

    @AfterEach
    fun clean() {
        val query = Query.query(Criteria.where("userId").`is`(4757839801))
        mongoTemplate.remove(query, MongoFinance::class.java)
            .block()
    }

    private val testMongoIncomeFinance: MongoFinance =
        MongoFinance(
            id = ObjectId(),
            userId = 4757839801,
            financeType = FinanceType.INCOME,
            amount = 100.0,
            description = "Test Description",
            date = Date()
        )


    @Test
    fun `should return expected finance when get all finance by request`() {
        //GIVEN
        financeRepository.save(testMongoIncomeFinance.toEntityFinance()).block()
        val userIdSave = testMongoIncomeFinance.userId
        val financeTypeSave = testMongoIncomeFinance.financeType

        val request = GetAllFinancesByIdRequest.newBuilder()
            .setUserId(userIdSave)
            .setFinanceType(financeTypeSave)
            .build()

        val expectedResponse = GetAllFinancesByIdResponse.newBuilder().apply {
            successBuilder.addAllFinance(listOf(testMongoIncomeFinance.toEntityFinance().toProtoFinance()))
        }.build()

        //WHEN
        val actualResponse = doRequest(
            NatsSubject.FinanceRequest.GET_ALL_FINANCES_BY_ID,
            request,
            GetAllFinancesByIdResponse.parser()
        )

        //THEN
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return expected message when delete finance`() {
        //GIVEN
        financeRepository.save(testMongoIncomeFinance.toEntityFinance()).block()
        val financeId = testMongoIncomeFinance.userId

        val request: DeleteFinanceByIdRequest = DeleteFinanceByIdRequest.newBuilder()
            .setUserId(financeId)
            .build()

        val expectedResponse = DeleteFinanceByIdResponse.newBuilder().apply {
            successBuilder.setMessage("Finance deleted successfully")
        }.build()

        //WHEN
        val actualResponse = doRequest(
            NatsSubject.FinanceRequest.DELETE_FINANCE,
            request,
            DeleteFinanceByIdResponse.parser()
        )

        //THEN
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return expected finance when save finance`() {
        //GIVEN
        financeRepository.save(testMongoIncomeFinance.toEntityFinance()).block()

        val request: CreateFinanceRequest = CreateFinanceRequest.newBuilder()
            .setFinance(testMongoIncomeFinance.toEntityFinance().toProtoFinance())
            .build()

        val expectedResponse = CreateFinanceResponse.newBuilder().apply {
            successBuilder.setFinance(testMongoIncomeFinance.toEntityFinance().toProtoFinance())
        }.build()

        //WHEN
        val actualResponse = doRequest(
            NatsSubject.FinanceRequest.CREATE_FINANCE,
            request,
            CreateFinanceResponse.parser()
        )

        //THEN
        assertThat(actualResponse).isEqualTo(expectedResponse)
    }

    @Test
    fun `should return expected current balance when get current balance request`(){
        //GIVEN
        val testExpenseFinance = MongoFinance(
            id = ObjectId(),
            userId = 4757839801,
            financeType = FinanceType.EXPENSE,
            amount = 50.0,
            description = "Test Description",
            date = Date()
        )
        financeRepository.run {
            save(testExpenseFinance.toEntityFinance()).block()
            save(testMongoIncomeFinance.toEntityFinance()).block()
        }
        val userIdSave = testMongoIncomeFinance.userId

        val request: GetCurrentBalanceRequest = GetCurrentBalanceRequest.newBuilder()
            .setUserId(userIdSave)
            .build()

        val expectedResponse = GetCurrentBalanceResponse.newBuilder().apply {
            successBuilder.setBalance(50.0)
        }.build()

        //WHEN
        val actualResponse = doRequest(
            NatsSubject.FinanceRequest.GET_CURRENT_BALANCE,
            request,
            GetCurrentBalanceResponse.parser()
        )

        //THEN
        assertThat(actualResponse).isEqualTo(expectedResponse)

    }

    private fun <RequestT : GeneratedMessageV3, ResponseT : GeneratedMessageV3> doRequest(
        subject: String,
        payload: RequestT,
        parser: Parser<ResponseT>,
    ): ResponseT {
        val response = natsConnection.requestWithTimeout(
            subject,
            payload.toByteArray(),
            Duration.ofSeconds(10L)
        )
        return parser.parseFrom(response.get().data)
    }
}
