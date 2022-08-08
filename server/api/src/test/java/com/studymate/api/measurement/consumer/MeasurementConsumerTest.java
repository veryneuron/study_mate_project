package com.studymate.api.measurement.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studymate.api.measurement.model.MeasurementData;
import com.studymate.api.measurement.repository.MeasurementDataRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
class MeasurementConsumerTest {
    @Autowired
    private MeasurementDataRepository measurementDataRepository;
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private MeasurementData measurementData = new MeasurementData();

    @BeforeEach
    void setUp() {
        measurementData.setTemperature(20.0f);
        measurementData.setHumidity(30.0f);
        measurementData.setTimestamp(LocalDateTime.now());
        measurementData.setRaspberrypiAddress("123.456.789.102");

        mapper.registerModule(new JavaTimeModule());
        rabbitAdmin.purgeQueue("measureQueue");
    }

    @AfterEach
    void tearDown() {
        rabbitAdmin.purgeQueue("measureQueue");
        measurementDataRepository.deleteAll();
    }

//    @Test
//    @DisplayName("Measurement Consumer normal Test")
//    void receiveMeasureData() throws JsonProcessingException {
//        String data = mapper.writeValueAsString(measurementData);
//        rabbitTemplate.convertAndSend("amq.topic", "measure_data", data);
//        await().atMost(3, TimeUnit.SECONDS).until(() -> measurementDataRepository.count() == 1);
//    }
}