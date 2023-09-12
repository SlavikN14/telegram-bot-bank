package com.ajaxproject.telegrambot.bot.commands

import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.bots.AbsSender

fun interface Command {
    fun handle(update: Update, absSender: AbsSender)
}
