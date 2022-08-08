package com.studymate.api.user.entity;

import com.studymate.api.study.entity.StudyTime;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
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
    @Column(name = "user_id", unique = true)
    @Length(min = 4, max = 20)
    private String userId;
    @Length(min = 4, max = 20)
    private String nickname;
    @NotNull
    @Column(name = "user_password")
    @Length(max = 60)
    private String userPassword;
    @Column(name = "temperature_setting")
    private Float temperatureSetting;
    @Column(name = "humidity_setting")
    private Float humiditySetting;
    @Column(name = "rasberrypi_address")
    @Length(max = 45)
    private String rasberrypiAddress;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_sn")
    @ToString.Exclude
    private List<StudyTime> studyTimes;

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
