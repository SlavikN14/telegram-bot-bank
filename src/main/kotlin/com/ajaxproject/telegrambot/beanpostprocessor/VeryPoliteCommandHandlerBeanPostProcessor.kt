package com.ajaxproject.telegrambot.beanpostprocessor

import com.ajaxproject.telegrambot.annotations.VeryPoliteCommand
import com.ajaxproject.telegrambot.annotations.VeryPoliteCommandHandler
import com.ajaxproject.telegrambot.bot.model.UserRequest
import com.ajaxproject.telegrambot.bot.service.TelegramService
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
class VeryPoliteCommandHandlerBeanPostProcessor : BeanPostProcessor {

    @Autowired
    private lateinit var telegramService: TelegramService

    private val beans = mutableMapOf<String, KClass<*>>()
    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any? {
        val beanClass = bean::class

        if (beanClass.java.isAnnotationPresent(VeryPoliteCommand::class.java)) {
            beans[beanName] = beanClass
        }
        return super.postProcessBeforeInitialization(bean, beanName)
    }

    override fun postProcessAfterInitialization(bean: Any, beanName: String): Any? {
        val beanClass = beans[beanName]
        return beanClass?.let {
            Proxy.newProxyInstance(
                beanClass.java.classLoader,
                beanClass.java.interfaces,
                InvocationHandlerImpl(bean, telegramService, beanClass)
            )
        } ?: bean
    }
}

class InvocationHandlerImpl(
    private val bean: Any,
    private val telegramService: TelegramService,
    private val originalBean: KClass<*>,
) : InvocationHandler {

    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any {
        val methodParams = args ?: emptyArray()
        if (hasVeryPoliteCommandHandlerAnnotation(originalBean, method)) {
            val userRequest = methodParams.find { it is UserRequest } as UserRequest
            val currentMessage = telegramService.sendMessage(
                chatId = userRequest.chatId,
                text = "Your request is very important to us, the best specialist process it"
            )
            method.invoke(bean, *methodParams)

            telegramService.deleteMessage(
                userRequest.chatId,
                currentMessage.messageId
            )
            return Any()
        }
        return method.invoke(bean, *methodParams)
    }

    private fun hasVeryPoliteCommandHandlerAnnotation(originalBean: KClass<*>, method: Method): Boolean {
        return originalBean.memberFunctions.any { beanMethod ->
            beanMethod.name == method.name &&
                    beanMethod.javaClass.typeParameters.contentEquals(method.javaClass.typeParameters) &&
                    beanMethod.findAnnotation<VeryPoliteCommandHandler>() != null
        }
    }
}
