package com.studymate.api.study.service;

import com.studymate.api.study.entity.StudyTime;
import com.studymate.api.user.entity.StudyUser;
import com.studymate.api.user.repository.StudyUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyService {
    private final StudyUserRepository studyUserRepository;
    public Duration calculateCurrentFocusStudyTime(String userId) {
        Optional<StudyUser> studyUser = studyUserRepository.findByUserId(userId);
        if (studyUser.isEmpty()) {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
        Optional<StudyTime> resultTime = studyUser.get().getLatestStudyTime();
        if (resultTime.isEmpty()) {
            return Duration.ZERO;
        } else {
            return resultTime.get().getCalculatedFocusTime();
        }
    }
    public Duration calculateCurrentNonFocusStudyTime(String userId) {
        Optional<StudyUser> studyUser = studyUserRepository.findByUserId(userId);
        if (studyUser.isEmpty()) {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
        Optional<StudyTime> resultTime = studyUser.get().getLatestStudyTime();
        if (resultTime.isEmpty()) {
            return Duration.ZERO;
        } else {
            return resultTime.get().getCalculatedNonFocusTime();
        }
    }

    public Duration calculateTotalFocusStudyTime(String userId) {
        Optional<StudyUser> studyUser = studyUserRepository.findByUserId(userId);
        if (studyUser.isEmpty()) {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
        return studyUser.get().getTotalFocusTime();
    }

    public Duration calculateTotalNonFocusStudyTime(String userId) {
        Optional<StudyUser> studyUser = studyUserRepository.findByUserId(userId);
        if (studyUser.isEmpty()) {
            log.info("UserId does not exist");
            throw new IllegalArgumentException("UserId does not exist");
        }
        return studyUser.get().getTotalStudyTime();
    }

}
