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
    @RabbitListener(queues = "studyTimeQueue")
    public void receiveStudyTime(String message) {
        log.info("StudyTimeQueue received: " + message);
    }
    @RabbitListener(queues = "studyRecordQueue")
    public void receiveStudyRecord(String message) {
        log.info("StudyRecordQueue received: " + message);
    }
    @RabbitListener(queues = "chattingQueue")
    public void receiveChatting(String message) {
        log.info("ChattingQueue received: " + message);
    }
}
