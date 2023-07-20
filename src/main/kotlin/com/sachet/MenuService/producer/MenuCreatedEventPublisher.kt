package com.sachet.MenuService.producer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.sachet.MenuService.model.Menu
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.header.Header
import org.apache.kafka.common.header.internals.RecordHeader
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.List
import java.util.concurrent.CompletableFuture
import kotlin.math.log

@Component
@Slf4j
class MenuCreatedEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String?, String>,
    private val objectMapper: ObjectMapper
) {
    @Value("\${spring.kafka.topic}")
    var topic: String? = null

    private val logger = KotlinLogging.logger {  }
    private fun buildProducerRecord(key: String?, value: String): ProducerRecord<String?, String> {
        val headers = List.of<Header>(RecordHeader("event-source", "scanner".toByteArray()))
        return ProducerRecord(topic, null, key, value, headers)
    }

    @Throws(JsonProcessingException::class)
    fun sendMenuCreatedEvent(menu: Menu): CompletableFuture<SendResult<String?, String>> {
        logger.info { "Sending the menu as message $menu" }
        val key = menu.id
        val value = objectMapper.writeValueAsString(menu)
        val producerRecord = buildProducerRecord(key, value)
        val completableFuture = kafkaTemplate.send(producerRecord)
        return completableFuture
            .whenComplete { result: SendResult<String?, String>, throwable: Throwable? ->
                if (throwable != null) {
                    handleFailure(key, value, throwable)
                } else {
                    handleSuccess(key, value, result)
                }
            }
    }

    private fun handleSuccess(key: String?, value: String, integerStringSendResult: SendResult<String?, String>) {
        println(
            "Message sent successfully for the key: $key, value: $value, partition is: ${integerStringSendResult.recordMetadata.partition()}"
        )
    }

    private fun handleFailure(key: String?, value: String, throwable: Throwable) {
        println(
            "Error sending the message and the exception is: ${throwable.message}"
        )
    }
}
