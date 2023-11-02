package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.properties.BotProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.DefaultAbsSender
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class TelegramService(
    private val botSender: FinanceBotSender,
) {

    fun sendMessage(chatId: Long, text: String, replyKeyboard: ReplyKeyboard? = null): Mono<Message> {
        val sendMessage = SendMessage.builder().also { msg ->
            msg.text(text)
            msg.chatId(chatId)
            msg.replyMarkup(replyKeyboard)
        }.build()
        return execute(sendMessage)
            .subscribeOn(Schedulers.boundedElastic())
    }

    private fun execute(botApiMethod: BotApiMethod<Message>): Mono<Message> {
        return Mono.fromCallable { botSender.execute(botApiMethod) }
            .onErrorResume { error ->
                log.error("Failed to execute bot method {}", botApiMethod, error)
                Mono.empty()
            }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TelegramService::class.java)
    }
}

@Component
class FinanceBotSender(properties: BotProperties) : DefaultAbsSender(DefaultBotOptions(), properties.token)
