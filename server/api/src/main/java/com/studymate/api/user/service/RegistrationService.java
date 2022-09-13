package com.studymate.api.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.user.dto.RasberrySettingDTO;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.repository.StudyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final StudyUserRepository studyUserRepository;
    private final ModelMapper modelMapper = new ModelMapper();
    private final ObjectMapper objectMapper;
    private final MqttClientConnection mqttClientConnection;

    public StudyUser findUser(final String userId) {
        Optional<StudyUser> userResult = studyUserRepository.findByUserId(userId);
        if (userResult.isPresent()) {
            return userResult.get();
        } else {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
    }

    public StudyUser setValue(final StudyUser studyUser) throws JsonProcessingException {
        Optional<StudyUser> oldUserValue = studyUserRepository.findByUserId(studyUser.getUserId());
        if (oldUserValue.isPresent()) {
            if (studyUser.getTemperatureSetting() != null) {
                oldUserValue.get().setTemperatureSetting(studyUser.getTemperatureSetting());
            }
            if (studyUser.getHumiditySetting() != null) {
                oldUserValue.get().setHumiditySetting(studyUser.getHumiditySetting());
            }
            if (studyUser.getRasberrypiAddress() != null) {
                oldUserValue.get().setRasberrypiAddress(studyUser.getRasberrypiAddress());
            }
            RasberrySettingDTO rasberrySettingDTO = modelMapper.map(oldUserValue.get(), RasberrySettingDTO.class);

            MqttMessage mqttMessage = new MqttMessage("setting"
                    , objectMapper.writeValueAsBytes(rasberrySettingDTO)
                    , QualityOfService.AT_LEAST_ONCE);
            mqttClientConnection.publish(mqttMessage);
            return studyUserRepository.save(oldUserValue.get());
        } else {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
    }
}
