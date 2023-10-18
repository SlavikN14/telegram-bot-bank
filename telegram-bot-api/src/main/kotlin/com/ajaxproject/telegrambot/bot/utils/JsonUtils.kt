package com.ajaxproject.telegrambot.bot.utils

import com.google.gson.GsonBuilder

object JsonUtils {

    val GSON = GsonBuilder()
        .setPrettyPrinting()
        .create()
}
