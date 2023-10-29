package com.ajaxproject.telegrambot.bot.beanpostprocessor

import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.BackToMainMenu
import com.ajaxproject.telegrambot.bot.beanpostprocessor.annotations.BackToMainMenuCommand
import com.ajaxproject.telegrambot.bot.enums.Buttons.BACK_TO_MENU_BUTTON
import com.ajaxproject.telegrambot.bot.enums.Commands
import com.ajaxproject.telegrambot.bot.enums.TextPropertyName.BACK_TO_MENU
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.TextService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
import com.ajaxproject.telegrambot.bot.utils.KeyboardUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.cglib.proxy.InvocationHandler
import org.springframework.cglib.proxy.Proxy
import org.springframework.stereotype.Component
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberFunctions

@Component
class BackToMainMenuBeanPostProcessor : BeanPostProcessor {
    @Autowired
    private lateinit var telegramService: TelegramService

    @Autowired
    private lateinit var textService: TextService

    private val beans = mutableMapOf<String, KClass<*>>()

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean::class

        if (beanClass.java.isAnnotationPresent(BackToMainMenu::class.java)) {
            beans[beanName] = beanClass
        }
        return super.postProcessBeforeInitialization(bean, beanName)
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        return beans[beanName]?.let { beanClass ->
            Proxy.newProxyInstance(
                beanClass.java.classLoader,
                beanClass.java.interfaces,
                InvocationHandlerMenuImpl(bean, telegramService, beanClass, textService)
            )
        } ?: bean
    }
}

class InvocationHandlerMenuImpl(
    private val bean: Any,
    private val telegramService: TelegramService,
    private val originalBean: KClass<*>,
    private val textService: TextService,
) : InvocationHandler {

    @Suppress("SpreadOperator")
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val methodParams = args ?: emptyArray()
        val result = method.invoke(bean, *methodParams)
        if (hasBackToMainMenuCommandAnnotation(originalBean, method)) {
            val updateRequest = methodParams.find { it is UpdateRequest } as UpdateRequest
            telegramService.sendMessage(
                chatId = updateRequest.chatId,
                text = textService.readText(BACK_TO_MENU.name),
                replyKeyboard = KeyboardUtils.run {
                    inlineKeyboard(
                        inlineRowKeyboard(
                            inlineButton(textService.readText(BACK_TO_MENU_BUTTON.name), Commands.MENU.command)
                        )
                    )
                }
            )
        }
        return result
    }

    private fun hasBackToMainMenuCommandAnnotation(originalBean: KClass<*>, method: Method): Boolean {
        return originalBean.memberFunctions.any { beanMethod ->
            beanMethod.name == method.name &&
                    beanMethod.javaClass.typeParameters.contentEquals(method.javaClass.typeParameters) &&
                    beanMethod.findAnnotation<BackToMainMenuCommand>() != null
        }
    }
}
