package com.ajaxproject.telegrambot.handler.impl.userhandlers

import com.ajaxproject.telegrambot.enums.Commands.DELETE_DATA
import com.ajaxproject.telegrambot.enums.TextPropertyName.DATA_IS_DELETED_TEXT
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.client.FinanceClient
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.TextService
import com.ajaxproject.telegrambot.service.UserService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeleteDataHandler(
    private val telegramService: TelegramMessageService,
    private val userService: UserService,
    private val financeClient: FinanceClient,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, DELETE_DATA.command)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        val chatId = dispatchRequest.chatId
        return userService.deleteUserById(chatId)
            .then(financeClient.removeAllFinances(chatId))
            .flatMap {
                telegramService.sendMessage(
                    chatId = chatId,
                    text = textService.getText(dispatchRequest.updateSession.localization, DATA_IS_DELETED_TEXT.name)
                )
            }.thenReturn(Unit)
    }
}
