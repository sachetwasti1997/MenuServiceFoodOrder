package com.sachet.MenuService.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sachet.MenuService.model.Menu;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class MenuCreatedEventPublisher {

    @Value("${spring.kafka.topic}")
    public String topic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public MenuCreatedEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    private ProducerRecord<String, String> buildProducerRecord(String key, String value) {
        List<Header> headers = List.of(new RecordHeader("event-source", "scanner".getBytes()));
        return new ProducerRecord<>(topic, null, key, value, headers);
    }

    public CompletableFuture<SendResult<String, String>>
    sendMenuCreatedEvent(Menu menu) throws JsonProcessingException {
        var key = menu.getId();
        var value = objectMapper.writeValueAsString(menu);
        var producerRecord = buildProducerRecord(key, value);
        var completableFuture = kafkaTemplate.send(producerRecord);
        return completableFuture
                .whenComplete(((result, throwable) -> {
                    if (throwable != null) {
                        handleFailure(key, value, throwable);
                    }else {
                        handleSuccess(key, value, result);
                    }
                }));
    }

    private void handleSuccess(String key, String value, SendResult<String, String> integerStringSendResult) {
        log.info("Message sent successfully for the key: {}, value: {}, partition is: {}",
                key, value, integerStringSendResult.getRecordMetadata().partition());
    }

    private void handleFailure(String key, String value, Throwable throwable) {
        log.error("Error sending the message and the exception is: {}", throwable.getMessage(), throwable);
    }

}
