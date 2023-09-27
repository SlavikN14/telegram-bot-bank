package com.ajaxproject.telegrambot.bot

import com.ajaxproject.telegrambot.bot.models.user.UserRequest
import com.ajaxproject.telegrambot.bot.properties.BotProperties
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class BankInfoBot(
    val dispatcher: Dispatcher,
    val userSessionService: UserSessionService,
    val botProperties: BotProperties,
) : TelegramLongPollingBot() {

    override fun getBotToken(): String = botProperties.token

    override fun getBotUsername(): String = botProperties.username

    override fun onUpdateReceived(update: Update) {
        if ((!update.hasMessage() || !update.message.hasText()) && !update.hasCallbackQuery()) {
            log.warn("Unexpected update from user")
            return
        }

        val chatId = when {
            update.message != null -> update.message.chatId
            update.callbackQuery != null -> update.callbackQuery.message.chatId
            else -> return
        }

        val userRequest = UserRequest(
            update = update,
            userSession = userSessionService.getSession(chatId),
            chatId = chatId
        )

        val isDispatched = dispatcher.dispatch(userRequest)

        if (!isDispatched) {
            log.warn(
                "Received unexpected update from user: userId={}, updateDetails={}",
                update.message.from.id,
                update.message.text
            )
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankInfoBot::class.java)
    }
}
