package com.ajaxproject.telegrambot.bot.commands

import org.slf4j.LoggerFactory
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender
import org.telegram.telegrambots.meta.exceptions.TelegramApiException


abstract class AbstractCommand(
    val commandId: String,
) : Command {

    protected fun sendMessage(sendMessage: BotApiMethod<Message>, absSender: AbsSender) {
        try {
            absSender.execute(sendMessage)
        } catch (e: TelegramApiException) {
            log.error("Cannot send message: {}", sendMessage, e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(AbstractCommand::class.java)
    }
}
