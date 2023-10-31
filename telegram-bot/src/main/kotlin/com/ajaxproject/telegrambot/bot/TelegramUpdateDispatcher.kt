package com.ajaxproject.telegrambot.bot

import com.ajaxproject.telegrambot.bot.handlers.UserRequestHandler
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Component
class TelegramUpdateDispatcher(
    private val handlers: List<UserRequestHandler>,
) {

    fun dispatch(updateRequest: UpdateRequest): Mono<Boolean> {
        return Flux.fromIterable(handlers)
            .filter { it.isApplicable(updateRequest) }
            .flatMap {
                it.handle(updateRequest)
                    .then(Mono.just(true))
            }
            .defaultIfEmpty(false)
            .toMono()
    }
}
