package com.studymate.api.user.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Digits;

@Data
public class RegistrationDTO {
    @Digits(integer = 3, fraction = 3, message = "Temperature setting must be a number")
    private Float temperatureSetting;
    @Digits(integer = 3, fraction = 3, message = "Humidity setting must be a number")
    private Float humiditySetting;
    @Length(max = 45, message = "Maximum length of address is 45")
    private String rasberrypiAddress;

}
