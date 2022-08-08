package com.studymate.api.measurement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Document(collection = "measurement_data")
public class MeasurementData {
    private Float temperature;
    private Float humidity;
    private LocalDateTime timestamp;
    @Length(max = 45, message = "Maximum length of address is 45")
    private String raspberrypiAddress;

}
