package com.sachet.MenuService.consumer

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.sachet.MenuService.exceptions.NotFoundException
import com.sachet.MenuService.model.OrderCancelledModal
import com.sachet.MenuService.repository.MenuRepository
import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.listener.AcknowledgingMessageListener
import org.springframework.kafka.support.Acknowledgment
import org.springframework.stereotype.Component

@Component
class OrderCancelledConsumer(
    private val menuRepository: MenuRepository,
    private val objectMapper: ObjectMapper
) :
    AcknowledgingMessageListener<String?, String?> {
    private val logger = KotlinLogging.logger {  }
    @KafkaListener(topics = ["\${spring.kafka.cancelorder}"],
        groupId = "\${spring.kafka.ordercancelconsumer.group-id}",
        containerFactory = "kafkaOrderCancelListenerContainerFactory")
    override fun onMessage(
        consumerRecord: ConsumerRecord<String?, String?>,
        acknowledgment: Acknowledgment?
    ) {
        logger.info("ORDER Consumer Record: {}", consumerRecord)
        try {
            val order = objectMapper.readValue(consumerRecord.value(), OrderCancelledModal::class.java)
            //Find the menu item which the order is reserving
            val menu = menuRepository.findById(order.menuId!!)

            //If no menu item throw error
            if (menu.isEmpty) {
                throw NotFoundException("The item you are trying to cancel not found")
            }

            //Mark the menu item as being reserved by setting orderId property
            val menuVar = menu.get()
            menuVar.orderId = null

            //save the menu item
            menuRepository.save(menuVar)

            //ack the message
            acknowledgment!!.acknowledge()
        } catch (e: JsonProcessingException) {
            throw RuntimeException(e)
        }
    }
}