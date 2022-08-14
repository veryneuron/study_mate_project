package com.studymate.api;

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

@SpringBootTest
class ApiApplicationTests {
    @Autowired
    private StudyUserRepository studyUserRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private StudyTimeRepository studyTimeRepository;
    @Autowired
    private StudyRecordRepository studyRecordRepository;
    private StudyUser studyUser;
    private final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
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
    }

    @Test
    @DisplayName("test JPA N+1 issue")
    @Transactional
    void contextLoads() {
//        studyUserRepository.deleteAll();

        Optional<StudyUser> studyUser = studyUserRepository.findByUserId("test");
        studyUser.get().getStudyTimes().get(0).getStudyRecords().forEach(studyRecord -> {
            System.out.println(studyRecord.getStartTimestamp());
            System.out.println(studyRecord.getEndTimestamp());
        } );
        studyUser.get().getStudyTimes().get(1).getStudyRecords().forEach(studyRecord -> {
            System.out.println(studyRecord.getStartTimestamp());
            System.out.println(studyRecord.getEndTimestamp());
        } );
    }

}
