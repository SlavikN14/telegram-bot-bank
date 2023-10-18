package com.ajaxproject.financeservice.controller

import com.google.protobuf.GeneratedMessageV3
import com.google.protobuf.Parser

interface NatsController<ReqT : GeneratedMessageV3, RespT : GeneratedMessageV3> {

    val subject: String

    val parser: Parser<ReqT>

    fun handle(request: ReqT): RespT
}
