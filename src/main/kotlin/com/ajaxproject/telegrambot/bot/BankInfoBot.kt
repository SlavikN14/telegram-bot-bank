package com.ajaxproject.telegrambot.bot

import com.ajaxproject.telegrambot.bot.model.UserRequest
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
@Component
class BankInfoBot(
    val dispatcher: Dispatcher,
    val userSessionService: UserSessionService,
) : TelegramLongPollingBot() {
    @Value("\${bot.username}")
    private val botUsername: String = ""

    @Value("\${bot.token}")
    private val token: String = ""
    override fun getBotToken(): String = token

    override fun getBotUsername(): String = botUsername

    override fun onUpdateReceived(update: Update) {
        if (update.hasMessage() && update.message.hasText()) {
            val textFromUser = update.message.text

            val userId = update.message.from.id
            val userFirstName = update.message.from.firstName

            log.info("[{}, {}] : {}", userId, userFirstName, textFromUser)

            val chatId = update.message.chatId
            val session = userSessionService.getSession(chatId)

            val userRequest = UserRequest(
                update = update,
                userSession = session,
                chatId = chatId
            )
            val dispatcher = dispatcher.dispatch(userRequest)
            if (!dispatcher) {
                log.warn("Unexpected update from user")
            }
        } else {
            log.warn("Unexpected update from user")
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankInfoBot::class.java)
    }
}
