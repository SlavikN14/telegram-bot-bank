package com.ajaxproject.telegrambot.bot

import com.ajaxproject.telegrambot.bot.properties.BotProperties
import com.ajaxproject.telegrambot.bot.service.UserSessionService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class FinanceBot(
    private val telegramUpdateDispatcher: TelegramUpdateDispatcher,
    private val userSessionService: UserSessionService,
    private val botProperties: BotProperties,
) : TelegramLongPollingBot(botProperties.token) {

    override fun getBotUsername(): String = botProperties.username

    override fun onUpdateReceived(update: Update) {
        Mono.just(update)
            .filter { update.hasMessage() || update.hasCallbackQuery() }
            .map { toUpdateRequest(it) }
            .map { telegramUpdateDispatcher.dispatch(it) }
            .doOnNext { isDispatched -> logNotDispatched(isDispatched, update) }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun logNotDispatched(isDispatched: Boolean, update: Update) {
        val userId: Long = (update.message?.from?.id ?: update.callbackQuery?.message?.from?.id) as Long
        val textFromUser: String = (update.message?.text ?: update.callbackQuery?.message?.text).toString()

        if (isDispatched) {
            log.info("Update from user: userId={}, updateDetails={}", userId, textFromUser)
        } else {
            log.warn("Received unexpected update from user: userId={}, updateDetails={}", userId, textFromUser)
        }
    }

    private fun toUpdateRequest(update: Update): UpdateRequest {
        val chatId: Long = (update.message?.chatId ?: update.callbackQuery?.message?.chatId) as Long

        return UpdateRequest(
            update = update,
            updateSession = userSessionService.getSession(chatId),
            chatId = chatId
        )
    }

    companion object {
        private val log = LoggerFactory.getLogger(FinanceBot::class.java)
    }
}
