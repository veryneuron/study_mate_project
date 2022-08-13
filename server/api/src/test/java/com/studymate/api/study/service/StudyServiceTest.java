package com.studymate.api.study.service;

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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ActiveProfiles("test")
@SpringBootTest
class StudyServiceTest {
    //Integration test with database
    @Autowired
    private AuthService authService;
    @Autowired
    private StudyService studyService;
    @Autowired
    private StudyUserRepository studyUserRepository;
    @Autowired
    private StudyTimeRepository studyTimeRepository;
    @Autowired
    private StudyRecordRepository studyRecordRepository;
    private StudyUser studyUser;
    private StudyUser studyUser_recording;
    private StudyUser studyUser_timing_notRecording;
    private final LocalDateTime currentTime = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        studyTimeRepository.deleteAll();
        studyRecordRepository.deleteAll();
        studyUserRepository.deleteAll();
        studyUser = StudyUser.builder()
                .userId("test").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(studyUser);
        Optional<StudyUser> savedUser = studyUserRepository.findByUserId("test");
        StudyTime studyTime1 = new StudyTime();
        studyTime1.setStartTimestamp(currentTime.minusHours(1).minusMinutes(50));
        studyTime1.setUserSerialNumber(savedUser.get().getUserSerialNumber());
        studyTime1.setUserId("test");
        StudyTime studyTime2 = new StudyTime();
        studyTime2.setStartTimestamp(currentTime.minusHours(3).minusMinutes(50));
        studyTime2.setUserSerialNumber(savedUser.get().getUserSerialNumber());
        studyTime2.setUserId("test");
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
        studyUserRepository.save(savedUser.get());

        // recording user
        studyUser_recording = StudyUser.builder()
                .userId("recordingTest").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(studyUser_recording);
        Optional<StudyUser> savedRecordingUser = studyUserRepository.findByUserId("recordingTest");
        StudyTime studyTime3 = new StudyTime();
        studyTime3.setStartTimestamp(currentTime.minusHours(3).minusMinutes(50));
        studyTime3.setUserSerialNumber(savedRecordingUser.get().getUserSerialNumber());
        studyTime3.setUserId("recordingTest");
        StudyTime studyTime4 = new StudyTime();
        studyTime4.setStartTimestamp(currentTime.minusHours(1).minusMinutes(50));
        studyTime4.setUserSerialNumber(savedRecordingUser.get().getUserSerialNumber());
        studyTime4.setUserId("recordingTest");
        StudyTime savedStudyTime3 = studyTimeRepository.save(studyTime3);
        StudyTime savedStudyTime4 = studyTimeRepository.save(studyTime4);

        StudyRecord studyRecord5 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime3.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(3).minusMinutes(50))
                .userId("recordingTest")
                .build();
        studyRecord5.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(30));
        StudyRecord studyRecord6 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime3.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(3).minusMinutes(20))
                .userId("recordingTest")
                .build();
        studyRecord6.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(10));
        StudyRecord studyRecord7 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime4.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
                .userId("recordingTest")
                .build();
        studyRecord7.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(30));
        StudyRecord studyRecord8 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime4.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(20))
                .userId("recordingTest")
                .build();

        studyTime3.addStudyRecordWithFocusTime(studyRecord5);
        studyTime3.addStudyRecordWithFocusTime(studyRecord6);
        studyTime3.setEndTimestampWithTotalTime(currentTime.minusHours(3).minusMinutes(10));

        savedStudyTime4.addStudyRecordWithFocusTime(studyRecord7);
        savedStudyTime4.addStudyRecordWithFocusTime(studyRecord8);

        savedRecordingUser.get().addStudyTime(studyTime3);
        savedRecordingUser.get().addStudyTime(studyTime4);
        studyUserRepository.save(savedRecordingUser.get());

        // timing but not recording user
        studyUser_timing_notRecording = StudyUser.builder()
                .userId("timingTest").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(studyUser_timing_notRecording);
        Optional<StudyUser> savedTimingUser = studyUserRepository.findByUserId("timingTest");
        StudyTime studyTime5 = new StudyTime();
        studyTime5.setStartTimestamp(currentTime.minusHours(3).minusMinutes(50));
        studyTime5.setUserSerialNumber(savedTimingUser.get().getUserSerialNumber());
        studyTime5.setUserId("timingTest");
        StudyTime studyTime6 = new StudyTime();
        studyTime6.setStartTimestamp(currentTime.minusHours(1).minusMinutes(50));
        studyTime6.setUserSerialNumber(savedTimingUser.get().getUserSerialNumber());
        studyTime6.setUserId("timingTest");
        StudyTime savedStudyTime5 = studyTimeRepository.save(studyTime5);
        StudyTime savedStudyTime6 = studyTimeRepository.save(studyTime6);

        StudyRecord studyRecord9 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime5.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(3).minusMinutes(50))
                .userId("timingTest")
                .build();
        studyRecord9.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(30));
        StudyRecord studyRecord10 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime5.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(3).minusMinutes(20))
                .userId("timingTest")
                .build();
        studyRecord10.setEndTimestampWithRecordTime(currentTime.minusHours(3).minusMinutes(10));
        StudyRecord studyRecord11 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime6.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(50))
                .userId("timingTest")
                .build();
        studyRecord11.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(30));
        StudyRecord studyRecord12 = StudyRecord.builder()
                .studyTimeSerialNumber(savedStudyTime6.getStudyTimeSerialNumber())
                .startTimestamp(currentTime.minusHours(1).minusMinutes(20))
                .userId("timingTest")
                .build();
        studyRecord12.setEndTimestampWithRecordTime(currentTime.minusHours(1).minusMinutes(10));

        savedStudyTime5.addStudyRecordWithFocusTime(studyRecord9);
        savedStudyTime5.addStudyRecordWithFocusTime(studyRecord10);
        savedStudyTime5.setEndTimestampWithTotalTime(currentTime.minusHours(3).minusMinutes(10));

        savedStudyTime6.addStudyRecordWithFocusTime(studyRecord11);
        savedStudyTime6.addStudyRecordWithFocusTime(studyRecord12);

        savedTimingUser.get().addStudyTime(savedStudyTime5);
        savedTimingUser.get().addStudyTime(savedStudyTime6);
        studyUserRepository.save(savedTimingUser.get());

        assertEquals(6, studyTimeRepository.count());
        assertEquals(12, studyRecordRepository.count());
        assertEquals(3, studyUserRepository.count());

        assertFalse(studyUser.isTiming());
        assertFalse(studyUser.isRecording());
        assertTrue(studyUser_recording.isTiming());
        assertTrue(studyUser_recording.isRecording());
        assertTrue(studyUser_timing_notRecording.isTiming());
        assertFalse(studyUser_timing_notRecording.isRecording());
    }
    @AfterEach
    void tearDown() {
        studyTimeRepository.deleteAll();
        studyRecordRepository.deleteAll();
        studyUserRepository.deleteAll();
    }

    @Test
    @DisplayName("test start study time to who don't have time")
    void addStartStudyTimeEmptyUser() {
        StudyUser emptyUser = StudyUser.builder()
                .userId("empty").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(emptyUser);
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime)
                .userId("empty").build();
        assertFalse(emptyUser.isTiming());
        assertFalse(emptyUser.isRecording());
        studyService.addStudyTime(studyDTO);
        studyUserRepository.findByUserId("empty").ifPresent(studyUser -> {
            assertEquals(1, studyUser.getStudyTimes().size());
            assertEquals(currentTime, studyUser.getStudyTimes().get(0).getStartTimestamp());
            assertNull(studyUser.getStudyTimes().get(0).getEndTimestamp());
            assertEquals(1, studyUser.getStudyTimes().get(0).getStudyRecords().size());
            assertEquals(currentTime, studyUser.getStudyTimes().get(0).getStudyRecords().get(0).getStartTimestamp());
            assertNull(studyUser.getStudyTimes().get(0).getStudyRecords().get(0).getEndTimestamp());
        });
        assertTrue(emptyUser.isTiming());
        assertTrue(emptyUser.isRecording());
    }

    @Test
    @DisplayName("test end study time to who don't have time exception")
    void addEndStudyTimeEmptyUser() {
        StudyUser emptyUser = StudyUser.builder()
                .userId("empty").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(emptyUser);
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime.minusMinutes(10))
                .endTimestamp(currentTime)
                .userId("empty").build();
        assertFalse(emptyUser.isTiming());
        assertFalse(emptyUser.isRecording());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyTime(studyDTO));
        assertEquals(studyDTO + "Illegal time", e.getMessage());
    }

    @Test
    @DisplayName("test start study time to normal user")
    void addStartStudyTimeNormalUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime)
                .userId("test").build();
        studyService.addStudyTime(studyDTO);
        studyUserRepository.findByUserId("test").ifPresent(studyUser -> {
            assertEquals(3, studyUser.getStudyTimes().size());
            assertEquals(currentTime, studyUser.getLatestStudyTime().get().getStartTimestamp());
            assertNull(studyUser.getLatestStudyTime().get().getEndTimestamp());
            assertEquals(1, studyUser.getLatestStudyTime().get().getStudyRecords().size());
            assertEquals(currentTime, studyUser.getLatestStudyTime().get().getStudyRecords().get(0).getStartTimestamp());
            assertNull(studyUser.getLatestStudyTime().get().getStudyRecords().get(0).getEndTimestamp());
        });
        assertTrue(studyUser.isTiming());
        assertTrue(studyUser.isRecording());
    }

    @Test
    @DisplayName("test end study time to normal user exception")
    void addEndStudyTimeNormalUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime.minusMinutes(10))
                .endTimestamp(currentTime)
                .userId("test").build();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyTime(studyDTO));
        assertEquals(studyDTO + "Illegal time", e.getMessage());
    }

    @Test
    @DisplayName("test start study time to recording user exception")
    void addStartStudyTimeRecordingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime.minusMinutes(10))
                .userId(studyUser_recording.getUserId()).build();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyTime(studyDTO));
        assertEquals(studyDTO + "Illegal time", e.getMessage());
    }

    @Test
    @DisplayName("test end start study time to recording user")
    void addEndStudyTimeRecordingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .endTimestamp(currentTime)
                .userId(studyUser_recording.getUserId()).build();
        studyService.addStudyTime(studyDTO);
        studyUserRepository.findByUserId(studyUser_recording.getUserId()).ifPresent(studyUser -> {
            assertEquals(2, studyUser.getStudyTimes().size());
            assertEquals(currentTime.minusHours(1).minusMinutes(50), studyUser.getLatestStudyTime().get().getStartTimestamp());
            assertEquals(currentTime, studyUser.getLatestStudyTime().get().getEndTimestamp());
            assertEquals(2, studyUser.getLatestStudyTime().get().getStudyRecords().size());
            assertEquals(currentTime.minusHours(1).minusMinutes(20), studyUser.getLatestStudyTime().get()
                    .getStudyRecords().get(studyUser.getLatestStudyTime().get().getStudyRecords().size() - 1).getStartTimestamp());
            assertEquals(currentTime, studyUser.getLatestStudyTime().get().getStudyRecords().get(
                    studyUser.getLatestStudyTime().get().getStudyRecords().size() - 1).getEndTimestamp());
        });
        assertFalse(studyUser_recording.isTiming());
        assertFalse(studyUser_recording.isRecording());
    }

    @Test
    @DisplayName("test start study time to timing but not recording user exception")
    void addStartStudyTimeTimingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime.minusMinutes(10))
                .userId(studyUser_timing_notRecording.getUserId()).build();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyTime(studyDTO));
        assertEquals(studyDTO + "Illegal time", e.getMessage());
    }

    @Test
    @DisplayName("test end study time to timing but not recording user")
    void addEndStudyTimeTimingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .endTimestamp(currentTime)
                .userId(studyUser_timing_notRecording.getUserId()).build();
        studyService.addStudyTime(studyDTO);
        studyUserRepository.findByUserId(studyUser_timing_notRecording.getUserId()).ifPresent(studyUser -> {
            assertEquals(2, studyUser.getStudyTimes().size());
            assertEquals(currentTime.minusHours(1).minusMinutes(50), studyUser.getLatestStudyTime().get().getStartTimestamp());
            assertEquals(currentTime, studyUser.getLatestStudyTime().get().getEndTimestamp());
            assertEquals(2, studyUser.getLatestStudyTime().get().getStudyRecords().size());
            assertEquals(currentTime.minusHours(1).minusMinutes(20), studyUser.getLatestStudyTime().get()
                    .getStudyRecords().get(studyUser.getLatestStudyTime().get().getStudyRecords().size() - 1).getStartTimestamp());
            assertEquals(currentTime.minusHours(1).minusMinutes(10), studyUser.getLatestStudyTime().get().getStudyRecords().get(
                    studyUser.getLatestStudyTime().get().getStudyRecords().size() - 1).getEndTimestamp());
        });
        assertFalse(studyUser_timing_notRecording.isTiming());
        assertFalse(studyUser_timing_notRecording.isRecording());
    }
    /*----------------------------------------addStudyRecord-------------------------------------------------*/

    @Test
    @DisplayName("test start study record to who don't have time exception")
    void addStartStudyRecordEmptyUser() {
        StudyUser emptyUser = StudyUser.builder()
                .userId("empty").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(emptyUser);
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime)
                .userId("empty").build();
        assertFalse(emptyUser.isTiming());
        assertFalse(emptyUser.isRecording());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyRecord(studyDTO));
        assertEquals(studyDTO + " - No study time", e.getMessage());
    }

    @Test
    @DisplayName("test end study record to who don't have time exception")
    void addEndStudyRecordEmptyUser() {
        StudyUser emptyUser = StudyUser.builder()
                .userId("empty").nickname("testnick").userPassword("testpassword").build();
        authService.createUser(emptyUser);
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime.minusMinutes(10))
                .endTimestamp(currentTime)
                .userId("empty").build();
        assertFalse(emptyUser.isTiming());
        assertFalse(emptyUser.isRecording());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyRecord(studyDTO));
        assertEquals(studyDTO + " - No study time", e.getMessage());
    }

    @Test
    @DisplayName("test start study record to not ongoing time user exception")
    void addStartStudyRecordNotOngoingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime)
                .userId("test").build();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyRecord(studyDTO));
        assertEquals(studyDTO + "Illegal time", e.getMessage());
    }

    @Test
    @DisplayName("test end study record to not ongoing user exception")
    void addEndStudyRecordNotOngoingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime.minusMinutes(10))
                .endTimestamp(currentTime)
                .userId("test").build();
        assertFalse(studyUser.isTiming());
        assertFalse(studyUser.isRecording());
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyRecord(studyDTO));
        assertEquals(studyDTO + "Illegal time", e.getMessage());
    }

    @Test
    @DisplayName("test start study record to recording user exception")
    void addStartStudyRecordToRecordingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime.minusMinutes(10))
                .userId(studyUser_recording.getUserId()).build();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyRecord(studyDTO));
        assertEquals(studyDTO + "Illegal time", e.getMessage());
    }

    @Test
    @DisplayName("test end study record to recording user")
    void addEndStudyRecordToRecordingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .endTimestamp(currentTime)
                .userId(studyUser_recording.getUserId()).build();
        studyService.addStudyRecord(studyDTO);
        studyUserRepository.findByUserId(studyUser_recording.getUserId()).ifPresent(studyUser -> {
            assertEquals(2, studyUser.getStudyTimes().size());
            assertEquals(currentTime.minusHours(1).minusMinutes(50), studyUser.getLatestStudyTime().get().getStartTimestamp());
            assertNull(studyUser.getLatestStudyTime().get().getEndTimestamp());
            assertEquals(2, studyUser.getLatestStudyTime().get().getStudyRecords().size());
            assertEquals(currentTime.minusHours(1).minusMinutes(20), studyUser.getLatestStudyTime().get()
                    .getLatestStudyRecord().getStartTimestamp());
            assertEquals(currentTime, studyUser.getLatestStudyTime().get().getLatestStudyRecord().getEndTimestamp());
        });
        assertTrue(studyUser_recording.isTiming());
        assertFalse(studyUser_recording.isRecording());
    }

    @Test
    @DisplayName("test start study record to timing but not recording user")
    void addStartStudyRecordToTimingNotRecordingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .startTimestamp(currentTime.minusMinutes(10))
                .userId(studyUser_timing_notRecording.getUserId()).build();
        studyService.addStudyRecord(studyDTO);
        studyUserRepository.findByUserId(studyUser_timing_notRecording.getUserId()).ifPresent(studyUser -> {
            assertEquals(2, studyUser.getStudyTimes().size());
            assertEquals(currentTime.minusHours(1).minusMinutes(50), studyUser.getLatestStudyTime().get().getStartTimestamp());
            assertNull(studyUser.getLatestStudyTime().get().getEndTimestamp());
            assertEquals(3, studyUser.getLatestStudyTime().get().getStudyRecords().size());
            assertEquals(currentTime.minusMinutes(10), studyUser.getLatestStudyTime().get()
                    .getLatestStudyRecord().getStartTimestamp());
            assertNull(studyUser.getLatestStudyTime().get().getLatestStudyRecord().getEndTimestamp());
        });
        assertTrue(studyUser_timing_notRecording.isTiming());
        assertTrue(studyUser_timing_notRecording.isRecording());
    }

    @Test
    @DisplayName("test end study record to timing but not recording user exception")
    void addEndStudyRecordToTimingNotRecordingUser() {
        StudyDTO studyDTO = StudyDTO.builder()
                .endTimestamp(currentTime)
                .userId(studyUser_timing_notRecording.getUserId()).build();
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> studyService.addStudyRecord(studyDTO));
        assertEquals(studyDTO + "Illegal time", e.getMessage());
    }

}