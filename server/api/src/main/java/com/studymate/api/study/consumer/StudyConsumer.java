package com.studymate.api.study.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.study.dto.StudyDTO;
import com.studymate.api.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyConsumer {
    private final StudyService studyService;
    private final ObjectMapper objectMapper;
    @RabbitListener(queues = "studyTimeQueue")
    public void receiveStudyTime(String message) {
        log.info("studyTimeQueue received: " + message);
        try {
            StudyDTO studyTime = objectMapper.readValue(message, StudyDTO.class);
            studyService.addStudyTime(studyTime);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: " + message);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }
    @RabbitListener(queues = "studyRecordQueue")
    public void receiveStudyRecord(String message) {
        log.info("studyRecordQueue received: " + message);
        try {
            StudyDTO studyRecord = objectMapper.readValue(message, StudyDTO.class);
            studyService.addStudyRecord(studyRecord);
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException: " + message);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
    }
}
