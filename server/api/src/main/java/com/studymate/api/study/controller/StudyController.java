package com.studymate.api.study.controller;

import com.studymate.api.study.dto.UserStatusDTO;
import com.studymate.api.study.service.StudyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/study")
@RequiredArgsConstructor
public class StudyController {
    private final StudyService studyService;

    @GetMapping("/{time}/{focus}")
    public ResponseEntity<Duration> retrieveStudyTime(@PathVariable String time, @PathVariable String focus,
                                                      @RequestParam String userId) {
        if (userId == null || time == null || focus == null) {
            throw new IllegalArgumentException("Illegal argument");
        }
        if (time.equals("current")) {
            if (focus.equals("focus")) {
                return ResponseEntity.ok(studyService.calculateCurrentFocusStudyTime(userId));
            } else if (focus.equals("non-focus")) {
                return ResponseEntity.ok(studyService.calculateCurrentNonFocusStudyTime(userId));
            } else {
                throw new IllegalArgumentException("Illegal argument");
            }
        } else if (time.equals("total")) {
            if (focus.equals("focus")) {
                return ResponseEntity.ok(studyService.calculateTotalFocusStudyTime(userId));
            } else if (focus.equals("non-focus")) {
                return ResponseEntity.ok(studyService.calculateTotalNonFocusStudyTime(userId));
            } else {
                throw new IllegalArgumentException("Illegal argument");
            }
        } else {
            throw new IllegalArgumentException("Illegal argument");
        }
    }

    @GetMapping
    public ResponseEntity<UserStatusDTO> checkUserStatus(@RequestParam List<String> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new IllegalArgumentException("Illegal argument");
        }
        return ResponseEntity.ok(UserStatusDTO.builder()
                .userStatus(studyService.getUserStatus(userIds))
                .build());
    }
}