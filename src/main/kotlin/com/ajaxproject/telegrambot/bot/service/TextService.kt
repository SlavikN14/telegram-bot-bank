package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.properties.TextProperties
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.Properties

@Service
class TextService(
    private val textProperties: TextProperties,
) {

    fun readText(property: String): String {
        return ClassPathResource(textProperties.path).inputStream
            .use { inputStream ->
                Properties().apply { load(inputStream) }
            }
            .getProperty(property)
            ?: throw FileNameNotFoundException("File ${textProperties.path} not found")
    }
}

fun Update.isTextMessage(): Boolean = hasMessage() && message.hasText()

class FileNameNotFoundException(message: String) : Exception(message)
