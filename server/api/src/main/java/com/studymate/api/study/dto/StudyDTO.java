package com.studymate.api.study.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class StudyDTO {
    private String userId;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
}
