package com.ajaxproject.telegrambot.bot.utils

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

object KeyboardUtils {

    fun inlineKeyboard(vararg rows: List<InlineKeyboardButton>): InlineKeyboardMarkup {
        return InlineKeyboardMarkup.builder()
            .keyboard(rows.toList())
            .build()
    }

    fun inlineRowKeyboard(vararg buttons: InlineKeyboardButton): List<InlineKeyboardButton> = listOf(*buttons)

    fun inlineButton(name: String, callbackData: String): InlineKeyboardButton {
        return InlineKeyboardButton.builder()
            .text(name)
            .callbackData(callbackData)
            .build()
    }
}
