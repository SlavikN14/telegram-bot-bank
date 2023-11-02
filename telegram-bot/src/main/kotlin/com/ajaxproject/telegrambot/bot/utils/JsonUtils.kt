package com.ajaxproject.telegrambot.bot.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonUtils {

    val GSON: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
}
