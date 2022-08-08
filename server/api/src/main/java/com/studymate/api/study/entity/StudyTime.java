package com.studymate.api.study.entity;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity(name = "study_time")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class StudyTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_time_sn")
    private Integer studyTimeSerialNumber;
    @NotNull
    @Column(name = "user_sn")
    private Integer userSerialNumber;
    @NotNull
    @Column(name = "start_timestamp")
    private LocalDateTime startTimestamp;
    @NotNull
    @Column(name = "end_timestamp")
    private LocalDateTime endTimestamp;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "study_time_sn")
    @ToString.Exclude
    private List<StudyRecord> studyRecords;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudyTime studyTime = (StudyTime) o;
        return studyTimeSerialNumber != null && Objects.equals(studyTimeSerialNumber, studyTime.studyTimeSerialNumber);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
