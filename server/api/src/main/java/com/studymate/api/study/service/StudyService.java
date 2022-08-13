package com.studymate.api.study.service;

import com.studymate.api.study.dto.StudyDTO;
import com.studymate.api.study.entity.StudyRecord;
import com.studymate.api.study.entity.StudyTime;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.repository.StudyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyUserRepository studyUserRepository;
    public Duration calculateCurrentFocusStudyTime(String userId) {
        Optional<StudyUser> studyUser = findStudyUser(userId);
        Optional<StudyTime> resultTime = studyUser.get().getLatestStudyTime();
        if (resultTime.isEmpty()) {
            return Duration.ZERO;
        } else {
            return resultTime.get().getCalculatedFocusTime();
        }
    }
    public Duration calculateCurrentNonFocusStudyTime(String userId) {
        Optional<StudyUser> studyUser = findStudyUser(userId);
        Optional<StudyTime> resultTime = studyUser.get().getLatestStudyTime();
        if (resultTime.isEmpty()) {
            return Duration.ZERO;
        } else {
            return resultTime.get().getCalculatedNonFocusTime();
        }
    }

    public Duration calculateTotalFocusStudyTime(String userId) {
        Optional<StudyUser> studyUser = findStudyUser(userId);
        return studyUser.get().getTotalFocusTime();
    }

    public Duration calculateTotalNonFocusStudyTime(String userId) {
        Optional<StudyUser> studyUser = findStudyUser(userId);
        return studyUser.get().getTotalStudyTime();
    }

    @Transactional
    public void addStudyTime(StudyDTO studyTimeDto) {
        Optional<StudyUser> studyUser = findStudyUser(studyTimeDto.getUserId());
        Optional<StudyTime> resultTime = studyUser.get().getLatestStudyTime();
        if (!studyUser.get().isTiming()
                && studyTimeDto.getEndTimestamp() == null
                && studyTimeDto.getStartTimestamp() != null) {
            studyUser.get().addStudyTime(StudyTime.builder()
                    .userSerialNumber(studyUser.get().getUserSerialNumber())
                    .startTimestamp(studyTimeDto.getStartTimestamp())
                    .userId(studyTimeDto.getUserId())
                    .build());
            StudyUser savedUser = studyUserRepository.save(studyUser.get());
            savedUser.getLatestStudyTime().get()
                    .addStudyRecordWithFocusTime(StudyRecord.builder()
                            .studyTimeSerialNumber(savedUser.getLatestStudyTime().get().getStudyTimeSerialNumber())
                            .userId(studyTimeDto.getUserId())
                            .startTimestamp(studyTimeDto.getStartTimestamp())
                            .build());
            studyUserRepository.save(savedUser);
        } else if (studyUser.get().isTiming() && studyTimeDto.getEndTimestamp() != null) {
            resultTime.get().setEndTimestampWithTotalTime(studyTimeDto.getEndTimestamp());
            if (studyUser.get().isRecording()) {
                resultTime.get().updateLatestStudyRecord(studyTimeDto.getEndTimestamp());
            }
            studyUserRepository.save(studyUser.get());
        } else {
            throw new IllegalArgumentException(studyTimeDto + "Illegal time");
        }
    }

    @Transactional
    public void addStudyRecord(StudyDTO studyTimeDto) {
        Optional<StudyUser> studyUser = findStudyUser(studyTimeDto.getUserId());
        Optional<StudyTime> resultTime = studyUser.get().getLatestStudyTime();
        if (resultTime.isEmpty()) {
            throw new IllegalArgumentException(studyTimeDto + " - No study time");
        }
        if (studyUser.get().isTiming()
                && !studyUser.get().isRecording()
                && studyTimeDto.getEndTimestamp() == null
                && studyTimeDto.getStartTimestamp() != null) {
            resultTime.get().addStudyRecordWithFocusTime(StudyRecord.builder()
                    .studyTimeSerialNumber(resultTime.get().getStudyTimeSerialNumber())
                    .userId(studyTimeDto.getUserId())
                    .startTimestamp(studyTimeDto.getStartTimestamp())
                    .build());
            studyUserRepository.save(studyUser.get());
        } else if (studyUser.get().isTiming() && studyUser.get().isRecording() && studyTimeDto.getEndTimestamp() != null) {
            resultTime.get().updateLatestStudyRecord(studyTimeDto.getEndTimestamp());
            studyUserRepository.save(studyUser.get());
        } else {
            throw new IllegalArgumentException(studyTimeDto + "Illegal time");
        }
    }

    private Optional<StudyUser> findStudyUser(String userId) {
        Optional<StudyUser> studyUser = studyUserRepository.findByUserId(userId);
        if (studyUser.isEmpty()) {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
        return studyUser;
    }
}
