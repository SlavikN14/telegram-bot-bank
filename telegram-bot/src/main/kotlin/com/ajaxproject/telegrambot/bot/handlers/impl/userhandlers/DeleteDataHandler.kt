package com.ajaxproject.telegrambot.bot.handlers.impl.userhandlers

import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.BackToMainMenu
import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.BackToMainMenuCommand
import com.ajaxproject.telegrambot.bot.enums.Commands.DELETE_DATA
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.DATA_IS_DELETED
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.FinanceRequestNatsService
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.UserService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
@BackToMainMenu
class DeleteDataHandler(
    private val telegramService: TelegramService,
    private val userService: UserService,
    private val financeRequestNatsService: FinanceRequestNatsService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, DELETE_DATA.command)
    }

    @BackToMainMenuCommand
    override fun handle(dispatchRequest: UpdateRequest) {
        val chatId = dispatchRequest.chatId
        userService.deleteUserById(chatId)
            .then(financeRequestNatsService.requestToRemoveAllFinances(chatId))
            .flatMap {
                Mono.fromSupplier {
                    telegramService.sendMessage(
                        chatId = chatId,
                        text = textService.readText(DATA_IS_DELETED.name)
                    )
                }.subscribeOn(Schedulers.boundedElastic())
            }
            .subscribe()
    }
}
