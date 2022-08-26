package com.studymate.api.measurement.controller;

import com.studymate.api.measurement.model.MeasurementData;
import com.studymate.api.measurement.repository.MeasurementDataRepository;
import com.studymate.api.study.entity.StudyRecord;
import com.studymate.api.study.entity.StudyTime;
import com.studymate.api.study.repository.StudyRecordRepository;
import com.studymate.api.study.repository.StudyTimeRepository;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.jwt.JwtTokenProvider;
import com.studymate.api.user.repository.StudyUserRepository;
import com.studymate.api.user.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class MeasurementControllerTest {
    //Integration Test
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private MeasurementDataRepository measurementDataRepository;
    @Autowired
    private StudyUserRepository studyUserRepository;
    @Autowired
    private StudyTimeRepository studyTimeRepository;
    @Autowired
    private StudyRecordRepository studyRecordRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    private String accessToken;
    private final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        measurementDataRepository.deleteAll();
        studyUserRepository.deleteAll();
        studyTimeRepository.deleteAll();
        studyRecordRepository.deleteAll();
        MeasurementData measurementData = MeasurementData.builder()
                .temperature(37.5f)
                .humidity(75.12f)
                .timestamp(LocalDateTime.now())
                .raspberrypiAddress("123.456.789.102")
                .userId("test")
                .build();
        MeasurementData measurementData2 = MeasurementData.builder()
                .temperature(25.6f)
                .humidity(82.10f)
                .timestamp(LocalDateTime.now())
                .raspberrypiAddress("123.456.789.102")
                .userId("test")
                .build();
        MeasurementData measurementData3 = MeasurementData.builder()
                .temperature(41.2f)
                .humidity(72.11f)
                .timestamp(LocalDateTime.now())
                .raspberrypiAddress("123.456.789.103")
                .userId("test10")
                .build();
        measurementDataRepository.save(measurementData);
        measurementDataRepository.save(measurementData2);
        measurementDataRepository.save(measurementData3);

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

        accessToken = authService.authenticate(studyUser.getUserId(), "testpassword");
    }
    @AfterEach
    void tearDown() {
        measurementDataRepository.deleteAll();
    }

    @Test
    @DisplayName("test without valid accessToken")
    void withoutTokenTest() throws Exception {
        mockMvc.perform(get("/measurement")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("test retrieveMeasureData normal case")
    void retrieveMeasureData() throws Exception {
        mockMvc.perform(get("/measurement")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));
    }

    @Test
    @DisplayName("test retrieveMeasureData error case")
    void retrieveMeasureDataErrorTest() throws Exception {
        String errorToken = jwtTokenProvider.createToken("test1");
        mockMvc.perform(get("/measurement")
                        .header("Authorization", "Bearer " + errorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Failed to get data - Illegal Argument")));
    }
}