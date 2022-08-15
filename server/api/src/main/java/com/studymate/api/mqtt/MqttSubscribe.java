package com.studymate.api.mqtt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttSubscribe {
    private final MqttClientConnection conn;

    @PostConstruct
    public void subscribe() {
        conn.subscribe("#", QualityOfService.AT_LEAST_ONCE, (payload) -> {
            String message = new String(payload.getPayload(), StandardCharsets.UTF_8);
            log.info(payload.getTopic() + " received: " + message);
        });
        log.info("successfully subscribed # topic");
    }
}
