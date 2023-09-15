package com.ajaxproject.telegrambot.bot.utils

import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.util.*

enum class Id {
    WELCOME,
    FUNCTIONS
}

@Component
class TextsUtils(
    @Value("\${text.path}")
    val path: String,
    @Value("\${text.fileName}")
    val fileName: String
) {

    private val textsMap: MutableMap<String, Properties> = mutableMapOf()

    fun getText(id: Id): String {
        return textsMap[fileName]?.getProperty(id.name)
            ?: throw FileNameNotFoundException("File name $fileName not found")
    }

    @PostConstruct
    fun readTexts() {
        val dir = File(path)
        if (dir.isDirectory()) {
            dir.listFiles()!!
                .filter { obj: File -> obj.isFile() }
                .forEach { file: File -> readText(file) }
        }
    }

    private fun readText(file: File) {
        try {
            FileInputStream(file).use { input ->
                val texts = Properties()
                texts.load(input)
                val fileName = file.getName()
                textsMap.put(fileName, texts)
            }
        } catch (e: IOException) {
            log.error("Failed to read text file: {}", file, e)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(TextsUtils::class.java)
    }
}

fun Update.isTextMessage(): Boolean {
    return this.hasMessage() && this.message.hasText()
}

fun Update.isTextMessage(text: String): Boolean {
    return this.hasMessage() && this.message.hasText() && this.message.text.equals(text)
}

class FileNameNotFoundException(message: String) : Exception(message)
