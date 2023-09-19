package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.sender.BankInfoBotSender
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

@Component
class TelegramService(
    val botSender: BankInfoBotSender,
) {

    fun sendMessage(chatId: Long, text: String, replyKeyboard: ReplyKeyboard? = null): Message {
        val sendMessage = SendMessage.builder().also { msg ->
            msg.text(text)
            msg.chatId(chatId)
            msg.replyMarkup(replyKeyboard)
        }.build()
        val responseMessage = execute(sendMessage)
        return responseMessage?: throw MessageIsNullException("Execute message is null")
    }

    fun deleteMessage(chatId: Long, messageId: Int) {
        val deleteMessage = DeleteMessage.builder().also { msg ->
            msg.messageId(messageId)
            msg.chatId(chatId)
        }.build()
        botSender.execute(deleteMessage)
    }

    private fun execute(botApiMethod: BotApiMethod<Message>): Message? {
        return botApiMethod.runCatching { botSender.execute(botApiMethod) }
            .onFailure { log.error("Failed to execute bot method {}", botApiMethod, it) }
            .getOrNull()
    }

    companion object {
        private val log = LoggerFactory.getLogger(TelegramService::class.java)
    }
}

class MessageIsNullException(message: String) : Exception(message)
