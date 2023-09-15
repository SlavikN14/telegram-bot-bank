package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.model.UserSession
import org.springframework.stereotype.Component

@Component
class UserSessionService {

    val userSessionMap: MutableMap<Long, UserSession> = mutableMapOf()

    fun getSession(chatId: Long): UserSession {
        return userSessionMap.getOrDefault(chatId, UserSession(chatId = chatId))
    }

    fun saveSession(chatId: Long, session: UserSession): UserSession {
        userSessionMap[chatId] = session
        return session
    }
}
