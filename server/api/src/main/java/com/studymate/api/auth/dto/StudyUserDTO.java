package com.studymate.api.auth.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class StudyUserDTO {
    @Length(max = 20, message = "아이디는 최대 20자리까지 가능합니다.")
    private String userId;
    @Length(max = 20, message = "닉네임은 최대 20자리까지 가능합니다.")
    private String nickname;
    @Length(max = 20, message = "비밀번호는 최대 20자리까지 가능합니다.")
    private String userPassword;
}
