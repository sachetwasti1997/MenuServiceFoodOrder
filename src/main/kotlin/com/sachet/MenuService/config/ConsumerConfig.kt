package com.sachet.MenuService.config

import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.listener.AcknowledgingMessageListener
import org.springframework.kafka.listener.ContainerProperties
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff


@Configuration
class ConsumerConfig {
    fun errorHandler(): DefaultErrorHandler {
        val fixedBackOff = FixedBackOff(1000L, 9)
        return DefaultErrorHandler(fixedBackOff)
    }

    @Bean
    fun kafkaListenerContainerFactory(
        configurer: ConcurrentKafkaListenerContainerFactoryConfigurer,
        factory: ConsumerFactory<Any?, Any?>?
    ): ConcurrentKafkaListenerContainerFactory<*, *> {
        val factory1 = ConcurrentKafkaListenerContainerFactory<Any, Any>()
        configurer.configure(factory1, factory)
        factory1.containerProperties.ackMode = ContainerProperties.AckMode.MANUAL
        factory1.setCommonErrorHandler(errorHandler())
        return factory1
    }
}
