package com.studymate.api.user.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class AuthDTO {
    @Length(max = 20, message = "Maximum length of userId is 20")
    private String userId;
    @Length(max = 20, message = "Maximum length of nickname is 20")
    private String nickname;
    @Length(max = 20, message = "Maximum length of userPassword is 20")
    private String userPassword;
}
