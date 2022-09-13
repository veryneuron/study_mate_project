package com.studymate.api.study.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StudyTimeTest {
    private StudyRecord studyRecord1;
    private StudyRecord studyRecord2;
    private StudyTime studyTime;

    @BeforeEach
    void setUp() {
        studyRecord1 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(50))
                .build();
        studyRecord1.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(1).minusMinutes(30));
        studyRecord2 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(20))
                .build();
        studyRecord2.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(1).minusMinutes(10));
        studyTime = new StudyTime();
        studyTime.setStartTimestamp(LocalDateTime.now().minusHours(1).minusMinutes(50));
        studyTime.addStudyRecordWithFocusTime(studyRecord1);
        studyTime.addStudyRecordWithFocusTime(studyRecord2);
    }

    @Test
    @DisplayName("test CalculateFocusTime finished study time, finished study record")
    void getCalculatedFocusTimeFinishTest() {

        StudyRecord studyRecord3 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(40))
                .endTimestamp(LocalDateTime.now().minusMinutes(10))
                .recordTime(Duration.between(LocalDateTime.now().minusMinutes(40), LocalDateTime.now().minusMinutes(10)))
                .build();

        studyTime.addStudyRecordWithFocusTime(studyRecord3);
        studyTime.setEndTimestampWithTotalTime(LocalDateTime.now());
        assertEquals(studyRecord1.getCalculatedRecordTime()
                        .plus(studyRecord2.getCalculatedRecordTime())
                        .plus(studyRecord3.getCalculatedRecordTime())
                , studyTime.getCalculatedFocusTime());
    }

    @Test
    @DisplayName("test CalculateFocusTime ongoing study time, finished study record")
    void getCalculatedFocusTimeOngoingRecordFinishTest() {

        StudyRecord studyRecord3 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(40))
                .endTimestamp(LocalDateTime.now().minusMinutes(10))
                .recordTime(Duration.between(LocalDateTime.now().minusMinutes(40), LocalDateTime.now().minusMinutes(10)))
                .build();

        studyTime.addStudyRecordWithFocusTime(studyRecord3);
        assertEquals(studyRecord1.getCalculatedRecordTime()
                        .plus(studyRecord2.getCalculatedRecordTime())
                        .plus(studyRecord3.getCalculatedRecordTime())
                , studyTime.getCalculatedFocusTime());
    }

    @Test
    @DisplayName("test CalculateFocusTime ongoing study time, ongoing study record")
    void getCalculatedFocusTimeOngoingRecordOngoingTest() {

        StudyRecord studyRecord3 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(40))
                .build();

        studyTime.addStudyRecordWithFocusTime(studyRecord3);
        assertEquals(studyRecord1.getCalculatedRecordTime()
                        .plus(studyRecord2.getCalculatedRecordTime())
                        .plus(studyRecord3.getCalculatedRecordTime())
                , studyTime.getCalculatedFocusTime());
    }

    // no case of finished study record and ongoing study record

    @Test
    @DisplayName("test CalculateNonFocusTime finished study time")
    void getCalculatedNonFocusTimeFinishedTest() {
        studyTime.setEndTimestampWithTotalTime(LocalDateTime.now().minusMinutes(10));
        assertEquals(Duration.between(LocalDateTime.now().minusHours(1).minusMinutes(50),
                LocalDateTime.now().minusMinutes(10)), studyTime.getCalculatedNonFocusTime());
    }

    @Test
    @DisplayName("test CalculateNonFocusTime ongoing study time")
    void getCalculatedNonFocusTimeOngoingTest() {
        assertEquals(Duration.between(LocalDateTime.now().minusHours(1).minusMinutes(50),
                LocalDateTime.now()), studyTime.getCalculatedNonFocusTime());
    }

    @Test
    @DisplayName("test setEndTimestampWithTotalTime timestamp exception")
    void setEndTimestampWithTotalTimeExceptionTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studyTime.setEndTimestampWithTotalTime(LocalDateTime.now().minusHours(3)));
        assertEquals("EndTimestamp is less than startTimestamp", exception.getMessage());
    }

    @Test
    @DisplayName("test addStudyRecordWithFocusTime previous timestamp exception")
    void addStudyRecordWithFocusTimeExceptionTest() {
        StudyRecord studyRecord3 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusHours(2).minusMinutes(20))
                .build();
        studyRecord3.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(1).minusMinutes(10));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studyTime.addStudyRecordWithFocusTime(studyRecord3));
        assertEquals("StartTimestamp is less than previous startTimestamp", exception.getMessage());
    }

    @Test
    @DisplayName("test updateLatestStudyRecord timestamp exception")
    void updateLatestStudyRecordTimestampExceptionTest() {
        StudyRecord studyRecord3 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        studyTime.addStudyRecordWithFocusTime(studyRecord3);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studyTime.updateLatestStudyRecord(LocalDateTime.now().minusHours(1)));
        assertEquals("EndTimestamp is less than startTimestamp", exception.getMessage());
    }

    @Test
    @DisplayName("test updateLatestStudyRecord argument exception")
    void updateLatestStudyRecordArgumentExceptionTest() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studyTime.updateLatestStudyRecord(null));
        assertEquals("LatestEndTimestamp is null", exception.getMessage());
    }

    @Test
    @DisplayName("test updateLatestStudyRecord StudyRecords exception")
    void updateLatestStudyRecordStudyRecordsExceptionTest() {
        StudyTime emptyStudyTime = new StudyTime();
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> emptyStudyTime.updateLatestStudyRecord(LocalDateTime.now()));
        assertEquals("StudyRecords are null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("test updateLatestStudyRecord normal case")
    void updateLatestStudyRecordTest() {
        StudyRecord studyRecord3 = StudyRecord.builder()
                .startTimestamp(LocalDateTime.now().minusMinutes(50))
                .build();
        studyTime.addStudyRecordWithFocusTime(studyRecord3);
        studyTime.updateLatestStudyRecord(LocalDateTime.now());
        assertEquals(studyRecord1.getCalculatedRecordTime()
                .plus(studyRecord2.getCalculatedRecordTime())
                .plus(studyRecord3.getCalculatedRecordTime()), studyTime.getCalculatedFocusTime());
    }

    @Test
    @DisplayName("test getLatestStudyRecord null case exception")
    void getLatestStudyRecordNullExceptionTest() {
        StudyTime nullStudyTime = new StudyTime();
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                nullStudyTime::getLatestStudyRecord);
        assertEquals("StudyRecords are null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("test getLatestStudyRecord empty case exception")
    void getLatestStudyRecordEmptyExceptionTest() {
        StudyTime emptyStudyTime = new StudyTime();
        emptyStudyTime.setStudyRecords(new ArrayList<>());
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                emptyStudyTime::getLatestStudyRecord);
        assertEquals("StudyRecords are null or empty", exception.getMessage());
    }

    @Test
    @DisplayName("test getLatestStudyRecord normal case exception")
    void getLatestStudyRecordNormalTest() {
        assertEquals(studyRecord2, studyTime.getLatestStudyRecord());
    }
}