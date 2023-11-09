package com.ajaxproject.telegrambot.handler.impl

import com.ajaxproject.telegrambot.enums.Buttons.DELETE_DATA_BUTTON
import com.ajaxproject.telegrambot.enums.Buttons.GET_CURRENCY_RATE_BUTTON
import com.ajaxproject.telegrambot.enums.Buttons.GET_CURRENT_BALANCE_BUTTON
import com.ajaxproject.telegrambot.enums.Buttons.MANAGE_FINANCES_MENU_BUTTON
import com.ajaxproject.telegrambot.enums.Commands.CURRENCY
import com.ajaxproject.telegrambot.enums.Commands.DELETE_DATA
import com.ajaxproject.telegrambot.enums.Commands.GET_CURRENT_BALANCE
import com.ajaxproject.telegrambot.enums.Commands.MANAGE_FINANCES
import com.ajaxproject.telegrambot.enums.Commands.MENU
import com.ajaxproject.telegrambot.enums.ConversationState
import com.ajaxproject.telegrambot.enums.TextPropertyName.MENU_TEXT
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.service.telegram.TelegramMessageService
import com.ajaxproject.telegrambot.service.TextService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.util.KeyboardUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MenuCommandHandler(
    private val telegramService: TelegramMessageService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, MENU.command)
                && (request.updateSession.state == ConversationState.CONVERSATION_STARTED
                || request.updateSession.state == ConversationState.WAITING_FOR_ADD_FINANCE)
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.getText(dispatchRequest.updateSession.localization, MENU_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardWithManyRows(
                    inlineButton(
                        textService.getText(
                            dispatchRequest.updateSession.localization,
                            GET_CURRENCY_RATE_BUTTON.name
                        ),
                        CURRENCY.command
                    ),
                    inlineButton(
                        textService.getText(
                            dispatchRequest.updateSession.localization,
                            MANAGE_FINANCES_MENU_BUTTON.name
                        ),
                        MANAGE_FINANCES.command
                    ),
                    inlineButton(
                        textService.getText(
                            dispatchRequest.updateSession.localization,
                            GET_CURRENT_BALANCE_BUTTON.name
                        ),
                        GET_CURRENT_BALANCE.command
                    ),
                    inlineButton(
                        textService.getText(
                            dispatchRequest.updateSession.localization,
                            DELETE_DATA_BUTTON.name
                        ),
                        DELETE_DATA.command
                    )
                )
            }
        ).thenReturn(Unit)
    }
}
