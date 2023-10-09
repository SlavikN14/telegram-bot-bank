package com.ajaxproject.telegrambot.bot.beanpostprocessor

import com.ajaxproject.telegrambot.bot.annotations.VeryPoliteCommand
import com.ajaxproject.telegrambot.bot.annotations.VeryPoliteCommandHandler
import com.ajaxproject.telegrambot.bot.service.TelegramService
import com.ajaxproject.telegrambot.bot.service.updatemodels.UpdateRequest
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
        return beans[beanName]?.let { beanClass ->
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

    @Suppress("SpreadOperator")
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        val methodParams = args ?: emptyArray()
        if (hasVeryPoliteCommandHandlerAnnotation(originalBean, method)) {
            val updateRequest = methodParams.find { it is UpdateRequest } as UpdateRequest
            val currentMessage = telegramService.sendMessage(
                chatId = updateRequest.chatId,
                text = REQUEST_HANDLING_MESSAGE
            )
            val result = method.invoke(bean, *methodParams)

            telegramService.deleteMessage(
                updateRequest.chatId,
                currentMessage.messageId
            )
            return result
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

    companion object {
        const val REQUEST_HANDLING_MESSAGE =
            "Your request is very important to us, the best specialist is processing it"
    }
}
