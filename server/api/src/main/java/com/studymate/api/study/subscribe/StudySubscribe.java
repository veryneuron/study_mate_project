package com.studymate.api.study.subscribe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.study.dto.StudyDTO;
import com.studymate.api.study.service.StudyService;
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
public class StudySubscribe {
    private final MqttClientConnection conn;
    private final StudyService studyService;
    private final ObjectMapper objectMapper;

    @PostConstruct
    public void subscribeStudyTime() {
        conn.subscribe("study_time", QualityOfService.AT_MOST_ONCE, (payload) -> {
            try {
                String message = new String(payload.getPayload(), StandardCharsets.UTF_8);
                StudyDTO studyTime = objectMapper.readValue(message, StudyDTO.class);
                studyService.addStudyTime(studyTime);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException: " + new String(payload.getPayload(), StandardCharsets.UTF_8));
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }
        });
        log.info("successfully subscribed study_time topic");
    }

    @PostConstruct
    public void subscribeStudyRecord() {
        conn.subscribe("study_record", QualityOfService.AT_MOST_ONCE, (payload) -> {
            try {
                String message = new String(payload.getPayload(), StandardCharsets.UTF_8);
                StudyDTO studyTime = objectMapper.readValue(message, StudyDTO.class);
                studyService.addStudyRecord(studyTime);
            } catch (JsonProcessingException e) {
                log.error("JsonProcessingException: " + new String(payload.getPayload(), StandardCharsets.UTF_8));
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage());
            }
        });
        log.info("successfully subscribed study_record topic");
    }
}
