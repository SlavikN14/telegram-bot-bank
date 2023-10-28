package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.Buttons.DELETE_DATA_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_CURRENCY_RATE
import com.ajaxproject.telegrambot.bot.enums.Buttons.MANAGE_FINANCES_MENU
import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_CURRENCY_BALANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.DELETE_DATA
import com.ajaxproject.telegrambot.bot.enums.Commands.CURRENCY
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_CURRENT_BALANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.MANAGE_FINANCES
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.MENU_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component

@Component
class MenuCommandHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, MENU.command) && CONVERSATION_STARTED == request.updateSession.state
    }

    override fun handle(dispatchRequest: UpdateRequest) {
        telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = textService.readText(MENU_TEXT.name),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboard(
                    inlineRowKeyboard(
                        inlineButton(textService.readText(GET_CURRENCY_RATE.name), CURRENCY.command)
                    ),
                    inlineRowKeyboard(
                        inlineButton(textService.readText(MANAGE_FINANCES_MENU.name), MANAGE_FINANCES.command)
                    ),
                    inlineRowKeyboard(
                        inlineButton(textService.readText(GET_CURRENCY_BALANCE.name), GET_CURRENT_BALANCE.command)
                    ),
                    inlineRowKeyboard(
                        inlineButton(textService.readText(DELETE_DATA_BUTTON.name), DELETE_DATA.command)
                    )
                )
            }
        )
    }
}
