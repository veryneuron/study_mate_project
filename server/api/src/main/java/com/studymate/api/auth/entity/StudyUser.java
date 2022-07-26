package com.studymate.api.auth.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity(name = "study_user")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StudyUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_sn")
    private Integer userSerialNumber;
    @NotNull
    @Column(name = "user_id")
    @Length(min = 4, max = 20)
    private String userId;
    @Length(min = 4, max = 20)
    private String nickname;
    @NotNull
    @Column(name = "user_password")
    @Length(min = 4, max = 20)
    private String userPassword;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudyUser studyUser = (StudyUser) o;
        return userId != null && Objects.equals(userId, studyUser.userId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
