package com.studymate.api.study.subscribe;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.studymate.api.study.dto.StudyDTO;
import com.studymate.api.study.entity.StudyRecord;
import com.studymate.api.study.entity.StudyTime;
import com.studymate.api.study.repository.StudyRecordRepository;
import com.studymate.api.study.repository.StudyTimeRepository;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.repository.StudyUserRepository;
import com.studymate.api.user.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.crt.mqtt.MqttClientConnection;
import software.amazon.awssdk.crt.mqtt.MqttMessage;
import software.amazon.awssdk.crt.mqtt.QualityOfService;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
//@Transactional - produce error.... why?
@ActiveProfiles("test")
class StudySubscribeTest {
    @Autowired
    private MqttClientConnection connection;
    @Autowired
    private AuthService authService;
    @Autowired
    private StudyUserRepository studyUserRepository;
    @Autowired
    private StudyTimeRepository studyTimeRepository;
    @Autowired
    private StudyRecordRepository studyRecordRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        mapper.registerModule(new JavaTimeModule());
        studyUserRepository.deleteAll();
        studyTimeRepository.deleteAll();
        studyRecordRepository.deleteAll();

        StudyUser studyUser = StudyUser.builder()
                .userId("test").nickname("testnick").userPassword("testpassword")
                .temperatureSetting(27.27f).humiditySetting(67.2f).rasberrypiAddress("123.456.789.102").build();
        authService.createUser(studyUser);
        Optional<StudyUser> savedUser = studyUserRepository.findByUserId("test");
        StudyTime studyTime1 = StudyTime.builder()
                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
                .userSerialNumber(savedUser.get().getUserSerialNumber())
                .userId(savedUser.get().getUserId())
                .build();
        StudyTime studyTime2 = StudyTime.builder()
                .startTimestamp(currentTime.minusHours(3).minusMinutes(50))
                .userSerialNumber(savedUser.get().getUserSerialNumber())
                .userId(savedUser.get().getUserId())
                .build();
        StudyTime savedStudyTime1 = studyTimeRepository.save(studyTime1);
        StudyTime savedStudyTime2 = studyTimeRepository.save(studyTime2);

        StudyRecord studyRecord1 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
                .userId("test")
                .build();
        studyRecord1.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(30));
        StudyRecord studyRecord2 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(20))
                .userId("test")
                .build();
        studyRecord2.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(10));
        StudyRecord studyRecord3 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(3).minusMinutes(50))
                .userId("test")
                .build();
        studyRecord3.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(30));
        StudyRecord studyRecord4 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(3).minusMinutes(20))
                .userId("test")
                .build();
        studyRecord4.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(10));

        studyTime1.addStudyRecordWithFocusTime(studyRecord1);
        studyTime1.addStudyRecordWithFocusTime(studyRecord2);
        studyTime1.setEndTimestampWithTotalTime(currentTime.minusHours(1).minusMinutes(10));

        studyTime2.addStudyRecordWithFocusTime(studyRecord3);
        studyTime2.addStudyRecordWithFocusTime(studyRecord4);
        studyTime2.setEndTimestampWithTotalTime(currentTime.minusHours(3).minusMinutes(20));

        savedUser.get().addStudyTime(studyTime1);
        savedUser.get().addStudyTime(studyTime2);
        studyUser = studyUserRepository.save(savedUser.get());

        assertEquals(2, studyTimeRepository.count());
        assertEquals(4, studyRecordRepository.count());
    }

    @AfterEach
    void tearDown() {
        studyUserRepository.deleteAll();
        studyTimeRepository.deleteAll();
        studyRecordRepository.deleteAll();
    }

    @Test
    @DisplayName("subscribeStudyTime normal case test")
    void subscribeStudyTimeNormalTest() throws JsonProcessingException {
        StudyDTO studyDTO = StudyDTO.builder()
                .userId("test")
                .startTimestamp(currentTime.minusMinutes(50))
                .build();
        MqttMessage message = new MqttMessage ("study_time"
                , mapper.writeValueAsBytes(studyDTO)
                , QualityOfService.AT_MOST_ONCE);
        CompletableFuture<Integer> published = connection.publish(message);
        await().atMost(3, TimeUnit.SECONDS).until(() -> published.get() != null);
        await().atMost(3, TimeUnit.SECONDS).until(() -> studyTimeRepository.count() == 3);
        assertEquals(3, studyUserRepository.findByUserId("test").get()
                .getStudyTimes().size());
        assertEquals(currentTime.minusMinutes(50).getMinute(), studyUserRepository.findByUserId("test").get()
                .getLatestStudyTime().get().getStartTimestamp().getMinute());
    }

    @Test
    @DisplayName("subscribeStudyRecord normal case test")
    void subscribeStudyRecordNormalTest() throws JsonProcessingException {
        Optional<StudyUser> savedUser = studyUserRepository.findByUserId("test");
        StudyTime studyTime = StudyTime.builder()
                .startTimestamp(currentTime.minusMinutes(50))
                .userSerialNumber(savedUser.get().getUserSerialNumber())
                .userId(savedUser.get().getUserId())
                .build();
        StudyTime savedStudyTime = studyTimeRepository.save(studyTime);
        StudyRecord studyRecord = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusMinutes(50))
                .userId("test")
                .build();
        studyRecord.setEndTimestampWithRecordTime(currentTime.minusMinutes(40));
        studyTime.addStudyRecordWithFocusTime(studyRecord);
        savedUser.get().addStudyTime(studyTime);
        studyUserRepository.save(savedUser.get());

        StudyDTO studyDTO = StudyDTO.builder()
                .userId("test")
                .startTimestamp(currentTime.minusMinutes(30))
                .build();
        MqttMessage message = new MqttMessage ("study_record"
                , mapper.writeValueAsBytes(studyDTO)
                , QualityOfService.AT_MOST_ONCE);
        CompletableFuture<Integer> published = connection.publish(message);
        await().atMost(3, TimeUnit.SECONDS).until(() -> published.get() != null);
        await().atMost(3, TimeUnit.SECONDS).until(() -> studyRecordRepository.count() == 6);
        assertEquals(3, studyTimeRepository.count());
        assertEquals(currentTime.minusMinutes(30).getMinute(), studyRecordRepository.findAll().get(5)
                .getStartTimestamp().getMinute());
        assertNull(studyRecordRepository.findAll().get(5).getEndTimestamp());
    }
}