package com.ajaxproject.telegrambot.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder

object JsonUtils {

    val GSON: Gson = GsonBuilder()
        .setPrettyPrinting()
        .create()
}
