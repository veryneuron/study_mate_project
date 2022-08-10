package com.studymate.api.user.dto;

import lombok.Data;

@Data
public class RasberrySettingDTO {
    private String userId;
    private Float temperatureSetting;
    private Float humiditySetting;
    private String rasberrypiAddress;
}
