package com.ajaxproject.telegrambot.bot.service

import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateSession
import org.springframework.stereotype.Service

@Service
class UserSessionService {

    private val updateSessionMap: MutableMap<Long, UpdateSession> = mutableMapOf()

    fun getSession(chatId: Long): UpdateSession {
        return updateSessionMap.getOrDefault(chatId, UpdateSession(chatId = chatId))
    }

    fun saveSession(chatId: Long, session: UpdateSession): UpdateSession {
        updateSessionMap[chatId] = session
        return session
    }
}
