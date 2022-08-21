package com.studymate.api;

import com.studymate.api.measurement.model.MeasurementData;
import com.studymate.api.measurement.repository.MeasurementDataRepository;
import com.studymate.api.study.entity.StudyRecord;
import com.studymate.api.study.entity.StudyTime;
import com.studymate.api.study.repository.StudyRecordRepository;
import com.studymate.api.study.repository.StudyTimeRepository;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.repository.StudyUserRepository;
import com.studymate.api.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@SpringBootTest
//class ApiApplicationTests {
//    @Autowired
//    private StudyUserRepository studyUserRepository;
//    @Autowired
//    private AuthService authService;
//    @Autowired
//    private StudyTimeRepository studyTimeRepository;
//    @Autowired
//    private StudyRecordRepository studyRecordRepository;
//    private StudyUser studyUser;
//    private final LocalDateTime currentTime = LocalDateTime.now();
//    @Autowired
//    private MeasurementDataRepository measurementDataRepository;

//    @BeforeEach
//    void setUp() {
//        studyUserRepository.deleteAll();
//        studyUser = StudyUser.builder()
//                .userId("test").nickname("testnick").userPassword("testpassword").build();
//        authService.createUser(studyUser);
//        Optional<StudyUser> savedUser = studyUserRepository.findByUserId("test");
//        StudyTime studyTime1 = StudyTime.builder()
//                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
//                .userSerialNumber(savedUser.get().getUserSerialNumber())
//                .userId(savedUser.get().getUserId())
//                .build();
//        StudyTime studyTime2 = StudyTime.builder()
//                .startTimestamp(currentTime.minusHours(3).minusMinutes(50))
//                .userSerialNumber(savedUser.get().getUserSerialNumber())
//                .userId(savedUser.get().getUserId())
//                .build();
//        StudyTime savedStudyTime1 = studyTimeRepository.save(studyTime1);
//        StudyTime savedStudyTime2 = studyTimeRepository.save(studyTime2);
//
//        StudyRecord studyRecord1 = StudyRecord.builder()
//                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
//                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
//                .userId("test")
//                .build();
//        studyRecord1.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(30));
//        StudyRecord studyRecord2 = StudyRecord.builder()
//                .studyTimeSerialNumber(savedStudyTime1.getStudyTimeSerialNumber())
//                .startTimestamp(currentTime.minusHours(1).minusMinutes(20))
//                .userId("test")
//                .build();
//        studyRecord2.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(10));
//        StudyRecord studyRecord3 = StudyRecord.builder()
//                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
//                .startTimestamp(currentTime.minusHours(3).minusMinutes(50))
//                .userId("test")
//                .build();
//        studyRecord3.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(30));
//        StudyRecord studyRecord4 = StudyRecord.builder()
//                .studyTimeSerialNumber(savedStudyTime2.getStudyTimeSerialNumber())
//                .startTimestamp(currentTime.minusHours(3).minusMinutes(20))
//                .userId("test")
//                .build();
//        studyRecord4.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(10));
//
//        studyTime1.addStudyRecordWithFocusTime(studyRecord1);
//        studyTime1.addStudyRecordWithFocusTime(studyRecord2);
//        studyTime1.setEndTimestampWithTotalTime(currentTime.minusHours(1).minusMinutes(10));
//
//        studyTime2.addStudyRecordWithFocusTime(studyRecord3);
//        studyTime2.addStudyRecordWithFocusTime(studyRecord4);
//        studyTime2.setEndTimestampWithTotalTime(currentTime.minusHours(3).minusMinutes(20));
//
//        savedUser.get().addStudyTime(studyTime1);
//        savedUser.get().addStudyTime(studyTime2);
//        studyUser = studyUserRepository.save(savedUser.get());
//
//        assertEquals(2, studyTimeRepository.count());
//        assertEquals(4, studyRecordRepository.count());
//    }

//    @Test
//    void mockUser() {
//        MeasurementData measurementData = MeasurementData.builder()
//                .temperature(30.5f)
//                .humidity(70.12f)
//                .timestamp(LocalDateTime.now().minusMinutes(10))
//                .raspberrypiAddress("123.456.789.102")
//                .userId("test")
//                .build();
//        MeasurementData measurementData2 = MeasurementData.builder()
//                .temperature(31.6f)
//                .humidity(71.10f)
//                .timestamp(LocalDateTime.now().minusMinutes(9))
//                .raspberrypiAddress("123.456.789.102")
//                .userId("test")
//                .build();
//        MeasurementData measurementData3 = MeasurementData.builder()
//                .temperature(32.5f)
//                .humidity(75.12f)
//                .timestamp(LocalDateTime.now().minusMinutes(8))
//                .raspberrypiAddress("123.456.789.102")
//                .userId("test")
//                .build();
//        MeasurementData measurementData4 = MeasurementData.builder()
//                .temperature(25.6f)
//                .humidity(82.10f)
//                .timestamp(LocalDateTime.now().minusMinutes(7))
//                .raspberrypiAddress("123.456.789.102")
//                .userId("test")
//                .build();
//        MeasurementData measurementData5 = MeasurementData.builder()
//                .temperature(37.5f)
//                .humidity(75.12f)
//                .timestamp(LocalDateTime.now().minusMinutes(6))
//                .raspberrypiAddress("123.456.789.102")
//                .userId("test")
//                .build();
//        MeasurementData measurementData6 = MeasurementData.builder()
//                .temperature(35.6f)
//                .humidity(72.10f)
//                .timestamp(LocalDateTime.now().minusMinutes(5))
//                .raspberrypiAddress("123.456.789.102")
//                .userId("test")
//                .build();
//        MeasurementData measurementData7 = MeasurementData.builder()
//                .temperature(37.5f)
//                .humidity(75.12f)
//                .timestamp(LocalDateTime.now().minusMinutes(4))
//                .raspberrypiAddress("123.456.789.102")
//                .userId("test")
//                .build();
//        MeasurementData measurementData8 = MeasurementData.builder()
//                .temperature(21.6f)
//                .humidity(87.10f)
//                .timestamp(LocalDateTime.now().minusMinutes(3))
//                .raspberrypiAddress("123.456.789.102")
//                .userId("test")
//                .build();
//        MeasurementData measurementData9 = MeasurementData.builder()
//                .temperature(41.2f)
//                .humidity(72.11f)
//                .timestamp(LocalDateTime.now())
//                .raspberrypiAddress("123.456.789.103")
//                .userId("test10")
//                .build();
//        measurementDataRepository.save(measurementData);
//        measurementDataRepository.save(measurementData2);
//        measurementDataRepository.save(measurementData3);
//        measurementDataRepository.save(measurementData4);
//        measurementDataRepository.save(measurementData5);
//        measurementDataRepository.save(measurementData6);
//        measurementDataRepository.save(measurementData7);
//        measurementDataRepository.save(measurementData8);
//        measurementDataRepository.save(measurementData9);
//    }
//}

//    @Test
//    @DisplayName("test JPA N+1 issue")
//    @Transactional
//    void contextLoads() {
//        studyUserRepository.deleteAll();
//
//        Optional<StudyUser> studyUser = studyUserRepository.findByUserId("test");
//        studyUser.get().getStudyTimes().get(0).getStudyRecords().forEach(studyRecord -> {
//            System.out.println(studyRecord.getStartTimestamp());
//            System.out.println(studyRecord.getEndTimestamp());
//        } );
//        studyUser.get().getStudyTimes().get(1).getStudyRecords().forEach(studyRecord -> {
//            System.out.println(studyRecord.getStartTimestamp());
//            System.out.println(studyRecord.getEndTimestamp());
//        } );
//    }