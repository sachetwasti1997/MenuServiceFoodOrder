package com.sachet.MenuService.consumer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.sachet.MenuService.exceptions.NotFoundException
import com.sachet.MenuService.model.OrderCreatedEventModel
import com.sachet.MenuService.repository.MenuRepository
import lombok.extern.slf4j.Slf4j
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.AcknowledgingMessageListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
@Slf4j
class OrderCreatedConsumer(
    private val menuRepository: MenuRepository,
    private val objectMapper: ObjectMapper) :
    AcknowledgingMessageListener<String?, String?> {

    private val logger = KotlinLogging.logger {  }
    @KafkaListener(topics = ["\${spring.kafka.ordertopic}"])
    override fun onMessage(
        consumerRecord: ConsumerRecord<String?, String?>,
        acknowledgment: Acknowledgment?
    ) {
        logger.info("ORDER Consumer Record: {}", consumerRecord)
        try {
            val order = objectMapper.readValue(consumerRecord.value(), OrderCreatedEventModel::class.java)
            //Find the menu item which the order is reserving
            val menu = menuRepository.findById(order.menuId!!)

            //If no menu item throw error
            if (menu.isEmpty) {
                throw NotFoundException("The item you are trying to reserve not found")
            }

            //Mark the menu item as being reserved by setting orderId property
            val menuVar = menu.get()
            menuVar.orderId = order.id

            //save the menu item
            menuRepository.save(menuVar)

            //ack the message
            acknowledgment!!.acknowledge()
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}
