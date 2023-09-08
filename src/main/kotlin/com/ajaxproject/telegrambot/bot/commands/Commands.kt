package com.ajaxproject.telegrambot.bot.commands

import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

const val START = "/start"

@Component
class StartAbstractCommand : AbstractCommand(START) {
    override fun handle(update: Update, absSender: AbsSender) {
        val message: SendMessage = SendMessage.builder()
            .chatId("${update.message.chatId}")
            .text("Hello")
            .build()
        sendMessage(message,absSender)
    }
}

