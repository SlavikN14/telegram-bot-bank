package com.ajaxproject.telegrambot.bot.handlers.impl.userhandlers

import com.ajaxproject.telegrambot.bot.enums.Commands.DELETE_DATA
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.DATA_IS_DELETED_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.FinanceClient
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class DeleteDataHandler(
    private val telegramService: TelegramService,
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
                    text = textService.textMap[dispatchRequest.updateSession.localization]
                        ?.get(DATA_IS_DELETED_TEXT.name)
                        .toString()
                )
            }.thenReturn(Unit)
    }
}
