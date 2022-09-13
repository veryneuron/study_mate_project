package com.studymate.api.study.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserStatusDTO {
    private List<UserStatus> userStatus;
}
