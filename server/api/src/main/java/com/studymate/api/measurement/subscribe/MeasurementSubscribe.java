package com.studymate.api.measurement.subscribe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.measurement.model.MeasurementData;
import com.studymate.api.measurement.repository.MeasurementDataRepository;
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
public class MeasurementSubscribe {

    private final MqttClientConnection conn;
    private final ObjectMapper objectMapper;
    private final MeasurementDataRepository measurementDataRepository;

    @PostConstruct
    public void subscribeMeasureData() {
        conn.subscribe("measure_data", QualityOfService.AT_MOST_ONCE, (payload) -> {
            try {
                String message = new String(payload.getPayload(), StandardCharsets.UTF_8);
                MeasurementData measurementData = objectMapper.readValue(message, MeasurementData.class);
                measurementDataRepository.save(measurementData);
                log.info("successfully saved measurement data: " + measurementData);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException: " + new String(payload.getPayload(), StandardCharsets.UTF_8));
            }
        });
        log.info("successfully subscribed measure_data topic");
    }
}
