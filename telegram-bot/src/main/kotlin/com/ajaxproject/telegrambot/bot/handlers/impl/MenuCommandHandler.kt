package com.ajaxproject.telegrambot.bot.handlers.impl

import com.ajaxproject.telegrambot.bot.enums.Buttons.DELETE_DATA_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_CURRENCY_RATE_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Buttons.GET_CURRENT_BALANCE_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Buttons.MANAGE_FINANCES_MENU_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Commands.CURRENCY
import com.ajaxproject.telegrambot.bot.enums.Commands.DELETE_DATA
import com.ajaxproject.telegrambot.bot.enums.Commands.GET_CURRENT_BALANCE
import com.ajaxproject.telegrambot.bot.enums.Commands.MANAGE_FINANCES
import com.ajaxproject.telegrambot.bot.enums.Commands.MENU
import com.ajaxproject.telegrambot.bot.enums.ConversationState.CONVERSATION_STARTED
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.MENU_TEXT
import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.textIsNotUploaded
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class MenuCommandHandler(
    private val telegramService: TelegramService,
    private val textService: TextService,
) : UserRequestHandler {

    override fun isApplicable(request: UpdateRequest): Boolean {
        return isCommand(request.update, MENU.command) && CONVERSATION_STARTED == request.updateSession.state
    }

    override fun handle(dispatchRequest: UpdateRequest): Mono<Unit> {
        val localizationText = textService.textMap[dispatchRequest.updateSession.localization]
        return telegramService.sendMessage(
            chatId = dispatchRequest.chatId,
            text = localizationText?.get(MENU_TEXT.name).textIsNotUploaded(),
            replyKeyboard = KeyboardUtils.run {
                inlineKeyboardWithManyRows(
                    inlineButton(
                        localizationText?.get(GET_CURRENCY_RATE_BUTTON.name).textIsNotUploaded(),
                        CURRENCY.command
                    ),
                    inlineButton(
                        localizationText?.get(MANAGE_FINANCES_MENU_BUTTON.name).textIsNotUploaded(),
                        MANAGE_FINANCES.command
                    ),
                    inlineButton(
                        localizationText?.get(GET_CURRENT_BALANCE_BUTTON.name).textIsNotUploaded(),
                        GET_CURRENT_BALANCE.command
                    ),
                    inlineButton(
                        localizationText?.get(DELETE_DATA_BUTTON.name).textIsNotUploaded(),
                        DELETE_DATA.command
                    )
                )
            }
        ).thenReturn(Unit)
    }
}
