package com.studymate.api.measurement.subscribe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studymate.api.measurement.model.MeasurementData;
import com.studymate.api.measurement.repository.MeasurementDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

import javax.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MeasurementSubscribeTest {
    @Autowired
    private MqttClientConnection connection;
    @Autowired
    private MeasurementDataRepository measurementDataRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        measurementDataRepository.deleteAll();
    }

    @Test
    @DisplayName("subscribeMeasureData normal case test")
    void subscribeMeasureDataNormalTest() throws JsonProcessingException {
        mapper.registerModule(new JavaTimeModule());
        MeasurementData measurementData = MeasurementData.builder()
                .temperature("37.5")
                .humidity("75.12")
                .timestamp(LocalDateTime.now())
                .raspberrypiAddress("123.456.789.102")
                .userId("test")
                .build();
        MqttMessage message = new MqttMessage ("measure_data"
                , mapper.writeValueAsBytes(measurementData)
                , QualityOfService.AT_MOST_ONCE);
        CompletableFuture<Integer> published = connection.publish(message);
        await().atMost(3, TimeUnit.SECONDS).until(() -> published.get() != null);
        assertEquals(1, measurementDataRepository.findByUserIdOrderByTimestampDesc("test").size());
    }

    @Test
    @DisplayName("subscribeMeasureData error case test")
    void subscribeMeasureDataErrorTest() {
        assertEquals(0, measurementDataRepository.count());
        MqttMessage message = new MqttMessage ("measure_data"
                , "error message".getBytes(StandardCharsets.UTF_8)
                , QualityOfService.AT_LEAST_ONCE);
        CompletableFuture<Integer> published = connection.publish(message);
        await().atMost(3, TimeUnit.SECONDS).until(() -> published.get() != null);
        assertEquals(0, measurementDataRepository.count());
    }
}