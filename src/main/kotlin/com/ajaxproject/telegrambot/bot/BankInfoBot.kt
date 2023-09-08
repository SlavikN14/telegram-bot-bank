package com.ajaxproject.telegrambot.bot

import com.ajaxproject.telegrambot.bot.commands.AbstractCommand
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Component
class BankInfoBot(
    @Qualifier("commandsMap")
    private val commandsMapping: Map<String, AbstractCommand>,
) : TelegramLongPollingBot() {

    @Value("\${credentials.botUsername}")
    private val botUsername: String = ""

    @Value("\${credentials.botToken}")
    private val token: String = ""

    override fun getBotUsername(): String = botUsername
    override fun getBotToken(): String = token

    override fun onUpdateReceived(update: Update?) {
        if (update?.hasMessage() == true) {
            val text = update.message.text
            commandsMapping[text]?.handle(update, this)
        } else if (update?.hasCallbackQuery() == true) {
            val callBack = update.callbackQuery.data
            log.debug("New request with callbackQuery: {}", callBack)
            commandsMapping[callBack]?.handle(update, this)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(BankInfoBot::class.java)
    }
}
