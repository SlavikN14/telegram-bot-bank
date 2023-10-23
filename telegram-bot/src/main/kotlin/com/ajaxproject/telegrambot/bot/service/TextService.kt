package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.properties.TextProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets
import java.util.Properties

@Service
class TextService(
    //TODO: try to rewrite to Reactor
    private val textProperties: TextProperties,
) {

//    fun readText(property: String, pathResource: String): Mono<String> {
//        return Mono.fromSupplier { readFile(property, pathResource) }
//    }

    lateinit var pathResource: String

    fun readText(property: String): String {
        return ClassPathResource(pathResource).inputStream
            .use {  Properties().apply {
                it.reader(StandardCharsets.UTF_8).use { reader ->
                    load(reader)
                }
            } }
            .getProperty(property)
            ?: throw FileNameNotFoundException("File ${textProperties.path} not found")

    }
}

fun Update.isTextMessage(): Boolean = hasMessage() && message.hasText()

class FileNameNotFoundException(message: String) : Exception(message)
