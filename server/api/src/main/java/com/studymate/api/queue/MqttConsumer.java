package com.studymate.api.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttConsumer {
    @RabbitListener(queues = "settingQueue")
    public void receiveSetting(String message) {
        log.info("SettingQueue received: " + message);
    }
    @RabbitListener(queues = "signalQueue")
    public void receiveSignal(String message) {
        log.info("signalQueue received: " + message);
    }
}
