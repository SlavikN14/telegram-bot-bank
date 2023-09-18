package com.ajaxproject.telegrambot.bot.utils

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

object KeyboardUtils {
    fun inlineKeyboard(rows: List<List<InlineKeyboardButton>>): InlineKeyboardMarkup {
        return InlineKeyboardMarkup.builder()
            .keyboard(rows)
            .build()
    }

    fun inlineRowKeyboard(vararg row: List<InlineKeyboardButton>): List<List<InlineKeyboardButton>> = listOf(*row)

    fun inlineButton(name: String, callbackData: String): InlineKeyboardButton {
        return InlineKeyboardButton.builder()
            .text(name)
            .callbackData(callbackData)
            .build()
    }

    fun replyKeyboard(vararg rows: KeyboardRow): ReplyKeyboardMarkup {
        return ReplyKeyboardMarkup.builder()
            .keyboard(rows.toList())
            .resizeKeyboard(true)
            .oneTimeKeyboard(false)
            .build()
    }

    fun rowReplyKeyboard(buttons: List<KeyboardButton>): KeyboardRow = KeyboardRow(buttons)
}
