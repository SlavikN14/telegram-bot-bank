package com.ajaxproject.telegrambot.configuration

import com.ajaxproject.telegrambot.RedisProperties
import com.ajaxproject.telegrambot.model.MongoCurrency
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableConfigurationProperties(RedisProperties::class)
class RedisConfiguration {

    @Bean
    fun reactiveRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
    ): ReactiveRedisTemplate<String, MongoCurrency> {
        val valueSerializer =
            Jackson2JsonRedisSerializer(MongoCurrency::class.java)

        val serializationContext = RedisSerializationContext
            .newSerializationContext<String, MongoCurrency>(StringRedisSerializer())
            .value(valueSerializer)
            .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }
}
