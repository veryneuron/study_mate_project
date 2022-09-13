package com.studymate.api.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.repository.StudyUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {
    RegistrationService registrationService;
    @Mock
    StudyUserRepository studyUserRepository;
    @Mock
    MqttClientConnection conn;
    StudyUser studyUser;

    @BeforeEach
    void setUp() {
        studyUser = new StudyUser();
        studyUser.setUserId("test");
        studyUser.setHumiditySetting(77.123f);
        studyUser.setTemperatureSetting(29.12f);
        studyUser.setRasberrypiAddress("123.456.789.0");

        registrationService = new RegistrationService(studyUserRepository, new ObjectMapper(), conn);

        assertNotNull(studyUserRepository);
        assertNotNull(conn);
    }

    //findUser

    @Test
    @DisplayName("test findUser normal case")
    void testNormalFindUser() {
        when(studyUserRepository.findByUserId("test")).thenReturn(Optional.of(studyUser));
        StudyUser user = registrationService.findUser("test");
        assertAll("user",
                () -> assertEquals(studyUser.getHumiditySetting(), user.getHumiditySetting()),
                () -> assertEquals(studyUser.getTemperatureSetting(), user.getTemperatureSetting()),
                () -> assertEquals(studyUser.getRasberrypiAddress(), user.getRasberrypiAddress())
        );
    }

    @Test
    @DisplayName("test findUser not existing case")
    void testNotExistingGetUser() {
        when(studyUserRepository.findByUserId("test1")).thenReturn(Optional.empty());
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> registrationService.findUser("test1"));
        assertEquals("UserId does not exist", error.getMessage());
    }

    //setValue

    @Test
    @DisplayName("test setValue normal case")
    void testNormalSetValue() throws JsonProcessingException {
        StudyUser newValue = new StudyUser();
        newValue.setUserId("test");
        newValue.setHumiditySetting(33.123f);
        newValue.setTemperatureSetting(13.12f);
        newValue.setRasberrypiAddress("098.876.543.2");
        when(studyUserRepository.save(any())).then(i -> i.getArgument(0, StudyUser.class));
        when(studyUserRepository.findByUserId("test")).thenReturn(Optional.of(studyUser));
        StudyUser user = registrationService.setValue(newValue);
        verify(conn, times(1)).publish(any());
        assertAll("user",
                () -> assertEquals(newValue.getHumiditySetting(), user.getHumiditySetting()),
                () -> assertEquals(newValue.getTemperatureSetting(), user.getTemperatureSetting()),
                () -> assertEquals(newValue.getRasberrypiAddress(), user.getRasberrypiAddress())
        );
    }

    @Test
    @DisplayName("test setValue empty UserID case")
    void testEmptyUserIdSetValue() {
        StudyUser newValue = new StudyUser();
        newValue.setUserId("test1");
        newValue.setHumiditySetting(33.123f);
        newValue.setTemperatureSetting(13.12f);
        newValue.setRasberrypiAddress("098.876.543.2");
        when(studyUserRepository.findByUserId("test1")).thenReturn(Optional.empty());
        IllegalArgumentException error = assertThrows(IllegalArgumentException.class, () -> registrationService.setValue(newValue));
        assertEquals("UserId does not exist", error.getMessage());
    }
}