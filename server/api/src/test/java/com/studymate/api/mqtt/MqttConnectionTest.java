package com.studymate.api.mqtt;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class MqttConnectionTest {
    @Autowired
    private MqttClientConnection connection;

    @Test
    @DisplayName("MQTT pub and sub test")
    void test() throws ExecutionException, InterruptedException {

        CountDownLatch countDownLatch = new CountDownLatch(1);
        CompletableFuture<Integer> subscribed = connection.subscribe("test", QualityOfService.AT_LEAST_ONCE, (message) -> {
            String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
            assertEquals("test message", payload);
            countDownLatch.countDown();
        });
        MqttMessage message = new MqttMessage ("test"
                , "test message".getBytes(StandardCharsets.UTF_8)
                , QualityOfService.AT_LEAST_ONCE);
        CompletableFuture<Integer> published = connection.publish(message);
        published.get();
        await().atMost(3, TimeUnit.SECONDS).until(() -> countDownLatch.getCount() == 0);
    }

}