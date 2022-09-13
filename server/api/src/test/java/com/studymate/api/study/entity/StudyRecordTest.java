package com.studymate.api.study.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StudyRecordTest {

    @Test
    @DisplayName("test CalculateStudyTime finished study record")
    void getCalculatedRecordTimeFinishTest() {
        StudyRecord studyRecord = new StudyRecord();
        LocalDateTime startTime = LocalDateTime.now().minusHours(1).minusMinutes(30);
        LocalDateTime endTime = LocalDateTime.now();
        studyRecord.setStartTimestamp(startTime);
        studyRecord.setEndTimestampWithRecordTime(endTime);
        assertEquals(Duration.between(startTime, endTime), studyRecord.getCalculatedRecordTime());
    }

    @Test
    @DisplayName("test CalculateStudyTime ongoing study record")
    void getCalculatedRecordTimeOngoingTest() {
        StudyRecord studyRecord = new StudyRecord();
        LocalDateTime startTime = LocalDateTime.now().minusHours(1).minusMinutes(30);
        studyRecord.setStartTimestamp(startTime);
        assertEquals(Duration.between(startTime, LocalDateTime.now()), studyRecord.getCalculatedRecordTime());
    }

    @Test
    @DisplayName("test CalculateStudyTime timestamp exception")
    void getCalculatedRecordTimeExceptionTest() {
        StudyRecord studyRecord = new StudyRecord();
        LocalDateTime startTime = LocalDateTime.now();
        studyRecord.setStartTimestamp(startTime);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> studyRecord.setEndTimestampWithRecordTime(LocalDateTime.now().minusHours(1).minusMinutes(30)));
        assertEquals("EndTimestamp is less than startTimestamp", exception.getMessage());
    }
}