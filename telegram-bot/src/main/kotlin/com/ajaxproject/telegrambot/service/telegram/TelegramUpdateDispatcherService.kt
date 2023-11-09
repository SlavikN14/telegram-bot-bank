package com.ajaxproject.telegrambot.service.telegram

import com.ajaxproject.telegrambot.BotProperties
import com.ajaxproject.telegrambot.handler.UserRequestHandler
import com.ajaxproject.telegrambot.service.UserSessionService
import com.ajaxproject.telegrambot.service.updatemodels.UpdateRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono

@Service
class TelegramUpdateDispatcherService(
    private val userSessionService: UserSessionService,
    private val botProperties: BotProperties,
    private val handlers: List<UserRequestHandler>,
) : TelegramLongPollingBot(botProperties.token) {

    override fun getBotUsername(): String = botProperties.username

    override fun onUpdateReceived(update: Update) {
        Mono.just(update)
            .filter { update.hasMessage() || update.hasCallbackQuery() }
            .map { toUpdateRequest(it) }
            .flatMap { dispatch(it) }
            .doOnNext { logNotDispatched(update) }
            .subscribeOn(Schedulers.boundedElastic())
            .subscribe()
    }

    private fun logNotDispatched(update: Update) {
        val userId: Long = (update.message?.from?.id ?: update.callbackQuery?.message?.from?.id) as Long
        val textFromUser: String = (update.message?.text ?: update.callbackQuery?.message?.text).toString()

        log.info("Update from user: userId={}, updateDetails={}", userId, textFromUser)
    }

    private fun toUpdateRequest(update: Update): UpdateRequest {
        val chatId: Long = (update.message?.chatId ?: update.callbackQuery?.message?.chatId) as Long

        return UpdateRequest(
            update = update,
            updateSession = userSessionService.getSession(chatId),
            chatId = chatId
        )
    }

    private fun dispatch(updateRequest: UpdateRequest): Mono<Boolean> {
        return Flux.fromIterable(handlers)
            .filter { it.isApplicable(updateRequest) }
            .flatMap {
                it.handle(updateRequest)
                    .thenReturn(true)
            }
            .defaultIfEmpty(false)
            .toMono()
    }

    companion object {
        private val log = LoggerFactory.getLogger(TelegramUpdateDispatcherService::class.java)
    }
}
