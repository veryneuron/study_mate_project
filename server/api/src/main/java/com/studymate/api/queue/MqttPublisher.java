package com.studymate.api.queue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttPublisher {
    private final RabbitTemplate rabbitTemplate;

    public void sendValueToClient(String routingKey, String value) {
        log.info("Sending " + value + "to " + routingKey);
        rabbitTemplate.convertAndSend("amq.topic", routingKey, value);
    }
}
