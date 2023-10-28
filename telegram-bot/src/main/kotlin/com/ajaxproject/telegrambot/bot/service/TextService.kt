package com.ajaxproject.telegrambot.bot.service

import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.objects.Update
import java.nio.charset.StandardCharsets
import java.util.Properties

@Service
class TextService {

    lateinit var pathResource: String

    fun readText(property: String): String {
        return ClassPathResource(pathResource).inputStream
            .use { Properties().apply {
                    it.reader(StandardCharsets.UTF_8).use { reader ->
                        load(reader)
                    }
                }
            }
            .getProperty(property)
            ?: throw FileNameNotFoundException("File $pathResource not found")
    }
}

fun Update.isTextMessage(): Boolean = hasMessage() && message.hasText()

class FileNameNotFoundException(message: String) : Exception(message)
