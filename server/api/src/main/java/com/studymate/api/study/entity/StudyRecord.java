package com.studymate.api.study.entity;

import com.vladmihalcea.hibernate.type.interval.PostgreSQLIntervalType;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.TypeDef;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "study_record")
@TypeDef(
        typeClass = PostgreSQLIntervalType.class,
        defaultForType = Duration.class
)
@Getter
@Setter
@ToString
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class StudyRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_record_sn")
    private Integer studyRecordSerialNumber;
    @Nullable
    @Column(name = "study_time_sn")
    private Integer studyTimeSerialNumber;
    @NotNull
    @Column(name = "start_timestamp")
    private LocalDateTime startTimestamp;
    @Nullable
    @Column(name = "end_timestamp")
    private LocalDateTime endTimestamp;
    @Nullable
    @Column(name = "record_time", columnDefinition = "interval")
    private Duration recordTime;
    @NotNull
    @Column(name = "user_id")
    private String userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StudyRecord that = (StudyRecord) o;
        return studyRecordSerialNumber != null && Objects.equals(studyRecordSerialNumber, that.studyRecordSerialNumber);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Duration getCalculatedRecordTime() {
        if (recordTime == null || endTimestamp == null) {
            return Duration.between(startTimestamp, LocalDateTime.now());
        } else {
            return recordTime;
        }
    }
    public void setEndTimestampWithRecordTime(LocalDateTime inputEndTimestamp) {
        if (inputEndTimestamp != null) {
            if (startTimestamp.isAfter(inputEndTimestamp)) {
                throw new IllegalArgumentException("EndTimestamp is less than startTimestamp");
            }
            endTimestamp = inputEndTimestamp;
            recordTime = Duration.between(startTimestamp, endTimestamp);
        }
    }
}
