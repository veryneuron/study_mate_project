package com.studymate.api.study.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudyDTO {
    private String userId;
    private LocalDateTime startTimestamp;
    private LocalDateTime endTimestamp;
}
