package com.studymate.api.measurement.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.measurement.model.MeasurementData;
import com.studymate.api.measurement.repository.MeasurementDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MeasurementConsumer {
    private final MeasurementDataRepository measurementDataRepository;
    private final ObjectMapper objectMapper;
    @RabbitListener(queues = "measureQueue")
    public void receiveMeasureData(String message) {
        log.info("MeasureQueue received: " + message);
        try {
            MeasurementData measurementData = objectMapper.readValue(message, MeasurementData.class);
            measurementDataRepository.save(measurementData);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: " + message);
        }
    }
}
