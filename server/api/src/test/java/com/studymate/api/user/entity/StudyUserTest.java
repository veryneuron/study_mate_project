package com.studymate.api.user.entity;

import com.studymate.api.study.entity.StudyRecord;
import com.studymate.api.study.entity.StudyTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class StudyUserTest {

    private StudyTime studyTime1;
    private StudyTime studyTime2;
    private StudyUser studyUser;

    @BeforeEach
    void setUp() {
        StudyRecord studyRecord1 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(50))
                .build();
        studyRecord1.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(1).minusMinutes(30));
        StudyRecord studyRecord2 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(20))
                .build();
        studyRecord2.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(1).minusMinutes(10));
        StudyRecord studyRecord3 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusHours(3).minusMinutes(50))
                .build();
        studyRecord3.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(3).minusMinutes(30));
        StudyRecord studyRecord4 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusHours(3).minusMinutes(20))
                .build();
        studyRecord4.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(3).minusMinutes(10));
        studyTime1 = new StudyTime();
        studyTime1.setStartTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(50));
        studyTime1.addStudyRecordWithFocusTime(studyRecord1);
        studyTime1.addStudyRecordWithFocusTime(studyRecord2);
        studyTime1.setEndTimestampWithTotalTime(LocalDateTime.now().minusHours(1).minusMinutes(10));
        studyTime2 = new StudyTime();
        studyTime2.setStartTimestamp(LocalDateTime.now().minusHours(3).minusMinutes(50));
        studyTime2.addStudyRecordWithFocusTime(studyRecord3);
        studyTime2.addStudyRecordWithFocusTime(studyRecord4);
        studyTime2.setEndTimestampWithTotalTime(LocalDateTime.now().minusHours(3).minusMinutes(20));
        studyUser = StudyUser.builder()
                .userId("test")
                .userPassword("testpassword")
                .build();
        studyUser.addStudyTime(studyTime1);
        studyUser.addStudyTime(studyTime2);
    }

    @Test
    @DisplayName("test getLatestStudyTime empty case")
    void getLatestStudyTimeEmptyTest() {
        StudyUser emptyStudyUser = new StudyUser();
        emptyStudyUser.setStudyTimes(new ArrayList<>());
        assertEquals(Optional.empty(), emptyStudyUser.getLatestStudyTime());
    }

    @Test
    @DisplayName("test getLatestStudyTime null case")
    void getLatestStudyTimeNullTest() {
        StudyUser emptyStudyUser = new StudyUser();
        assertEquals(Optional.empty(), emptyStudyUser.getLatestStudyTime());
    }

    @Test
    @DisplayName("test getLatestStudyTime normal case")
    void getLatestStudyTimeTest() {
        assertEquals(studyTime1, studyUser.getLatestStudyTime().get());
    }

    @Test
    @DisplayName("test getTotalStudyTime empty case")
    void getTotalStudyTimeEmptyTest() {
        StudyUser emptyStudyUser = new StudyUser();
        emptyStudyUser.setStudyTimes(new ArrayList<>());
        assertEquals(Duration.ZERO, emptyStudyUser.getTotalStudyTime());
    }

    @Test
    @DisplayName("test getTotalStudyTime null case")
    void getTotalStudyTimeNullTest() {
        StudyUser emptyStudyUser = new StudyUser();
        assertEquals(Duration.ZERO, emptyStudyUser.getTotalStudyTime());
    }

    @Test
    @DisplayName("test getTotalStudyTime finished time case")
    void getTotalStudyTimeFinishedTest() {
        assertEquals(studyTime1.getCalculatedNonFocusTime()
                .plus(studyTime2.getCalculatedNonFocusTime()), studyUser.getTotalStudyTime());
    }

    @Test
    @DisplayName("test getTotalStudyTime ongoing time case")
    void getTotalStudyTimeOngoingTest() {
        StudyRecord studyRecord5 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        studyRecord5.setEndTimestampWithRecordTime(LocalDateTime.now().minusMinutes(30));
        StudyRecord studyRecord6 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(20))
                .build();
        StudyTime studyTime3 = new StudyTime();
        studyTime3.setStartTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(50));
        studyTime3.addStudyRecordWithFocusTime(studyRecord5);
        studyTime3.addStudyRecordWithFocusTime(studyRecord6);
        studyUser.addStudyTime(studyTime3);
        assertEquals(studyTime1.getCalculatedNonFocusTime()
                .plus(studyTime2.getCalculatedNonFocusTime())
                .plus(studyTime3.getCalculatedNonFocusTime()), studyUser.getTotalStudyTime());
    }


    @Test
    @DisplayName("test getTotalFocusTime empty case")
    void getTotalFocusTimeEmptyTest() {
        StudyUser emptyStudyUser = new StudyUser();
        emptyStudyUser.setStudyTimes(new ArrayList<>());
        assertEquals(Duration.ZERO, emptyStudyUser.getTotalFocusTime());
    }

    @Test
    @DisplayName("test getTotalFocusTime null case")
    void getTotalFocusTimeNullTest() {
        StudyUser emptyStudyUser = new StudyUser();
        assertEquals(Duration.ZERO, emptyStudyUser.getTotalFocusTime());
    }

    @Test
    @DisplayName("test getTotalFocusTime finished time case")
    void getTotalFocusTimeFinishedTest() {
        assertEquals(studyTime1.getCalculatedFocusTime()
                .plus(studyTime2.getCalculatedFocusTime()), studyUser.getTotalFocusTime());
    }

    @Test
    @DisplayName("test getTotalFocusTime ongoing time case")
    void getTotalFocusTimeOngoingTest() {
        StudyRecord studyRecord5 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        studyRecord5.setEndTimestampWithRecordTime(LocalDateTime.now().minusMinutes(30));
        StudyRecord studyRecord6 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(20))
                .build();
        StudyTime studyTime3 = new StudyTime();
        studyTime3.setStartTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(50));
        studyTime3.addStudyRecordWithFocusTime(studyRecord5);
        studyTime3.addStudyRecordWithFocusTime(studyRecord6);
        studyUser.addStudyTime(studyTime3);
        assertEquals(studyTime1.getCalculatedFocusTime()
                .plus(studyTime2.getCalculatedFocusTime())
                .plus(studyTime3.getCalculatedFocusTime()), studyUser.getTotalFocusTime());
    }
}